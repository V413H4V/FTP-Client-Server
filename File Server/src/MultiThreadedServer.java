

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class MultiThreadedServer {

	private int portNumber;
	private ServerSocket serverSocket;
	private String command;
	private static final String SERVER_DIR = ".\\ServerDirectory\\";
	private static final String ACK_TAG = "ack";
	private static final String DOWNLOAD_TAG = "download";
	private static final String UPLOAD_TAG = "upload";
	private static final String DELETE_TAG = "delete";
	private static final String RENAME_TAG = "rename";
	private static final String FILELIST_TAG = "list";
	private static final String STOP_TAG = "stop";
	private static final String CALCULATE_PI_TAG = "calculate_pi";
	private static final String ADD_TAG = "add";
	private static final String SORT_TAG = "sort";
	private static final String MATRIX_MULTIPLICATION_TAG = "matrix_multiplication";
	private static final String FILESYNC_TAG = "sync";

	public MultiThreadedServer(int portNumber) {
		super();
		this.portNumber = portNumber;
	}
	
	public void initiateServer() throws IOException{
		serverSocket = new ServerSocket(portNumber);
		File homeDir = new File(SERVER_DIR);
		if(!homeDir.exists())
			homeDir.mkdirs();
		while(true){
			System.out.println("Server listening at: "+serverSocket.getLocalSocketAddress().toString()+", Port: "+serverSocket.getLocalPort());
			Socket clientSocket = serverSocket.accept();
			System.out.println("Connected to: "+clientSocket.getRemoteSocketAddress().toString());
			try{
				new Thread(){
					@Override
					public void run(){
						try {
							startServer(clientSocket);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return;
					}
				}.start();
				
			}catch(Exception e){
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void startServer(Socket clientSocket) throws IOException{

		try { 
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// sending ack
			out.println(ACK_TAG);

			while(true){
				String fileName;
				command = in.readLine();
				System.out.println("Command received: " + command);
				ArrayList<String> splitCommand = new ArrayList<String>();
				if(command.contains(":")){
					String[] temp = command.split(":");
					for (String str : temp){
						splitCommand.add(str);
					}
				}else{
					splitCommand.add(command);
				}
				if(splitCommand.get(0).equalsIgnoreCase(DOWNLOAD_TAG)){
					if(splitCommand.size() >= 2){
						fileName = splitCommand.get(1);
						byte[] fileBytes = fileDownload(fileName);
						ObjectTransfer obj = new ObjectTransfer();
						obj.setSize(fileBytes.length);
						obj.setBytes(fileBytes);
						ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
						oos.writeObject(obj);
//						oos.close();
					}else{
						System.out.println(DOWNLOAD_TAG + ": Invalid Command format!");
						out.println(DOWNLOAD_TAG + ": Invalid Command format!");
					}
				}else if(splitCommand.get(0).equalsIgnoreCase(UPLOAD_TAG)){
					if(splitCommand.size() >= 3){
						fileName = splitCommand.get(1);
						int fileSize = Integer.parseInt(splitCommand.get(2));
						out.println("Ok");
						ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
						ObjectTransfer obj = (ObjectTransfer) ois.readObject();
						if(fileUpload(fileName, obj.getBytes())){
							System.out.println(fileName+" uploaded successfully!");
							out.println(fileName+" uploaded successfully!");
						}
//						ois.close();
						// call file_list after this
					}else{
						System.out.println(UPLOAD_TAG + ": Invalid Command format!");
						out.println(UPLOAD_TAG + ": Invalid Command format!");
					}
				}else if(splitCommand.get(0).equalsIgnoreCase(DELETE_TAG)){
					if(splitCommand.size() >= 2){
						fileName = splitCommand.get(1);
						if(deleteFile(fileName)){
							System.out.println(fileName + " deleted successfully!");
							out.println(fileName + " deleted successfully!");
						}else{
							out.println("Could not delete this file!");
						}

					}else{
						System.out.println(DELETE_TAG + ": Invalid Command format!");
						out.println(DELETE_TAG + ": Invalid Command format!");
					}
				}else if(splitCommand.get(0).equalsIgnoreCase(RENAME_TAG)){
					if(splitCommand.size() >= 3){
						fileName = splitCommand.get(1);
						String newName = splitCommand.get(2);
						if(renameFile(fileName, newName)){
							System.out.println(fileName + " renamed to "+newName+" successfully!");
							out.println(fileName + " renamed to "+newName+" successfully!");
						}else{
							out.println("Could not rename this file!");
						}

					}else{
						System.out.println(DELETE_TAG + ": Invalid Command format!");
						out.println(DELETE_TAG + ": Invalid Command format!");
					}
				}else if(splitCommand.get(0).equalsIgnoreCase(FILELIST_TAG)){
					String[] fileList = getFileList();
					if(fileList != null){
						StringBuilder listResponse = new StringBuilder("");
						for(String str : fileList){
							listResponse.append(str+":");
						}
						out.println(listResponse);
					}else{
						out.println("Could not list Server directory!");
					}
				}else if(splitCommand.get(0).equalsIgnoreCase(CALCULATE_PI_TAG)){
					out.println(String.valueOf(calculatePi(100000)));

				}else if(splitCommand.get(0).equalsIgnoreCase(ADD_TAG)){
					try{
						if(splitCommand.size() >= 3){
							int x = Integer.parseInt(splitCommand.get(1));
							int y = Integer.parseInt(splitCommand.get(2));
							out.println(add(x,y));
						}else{
							out.println("Invalid number format!");
						}
					}catch(NumberFormatException e){
						out.println("Invalid number format!");
					}

				}else if(splitCommand.get(0).equalsIgnoreCase(SORT_TAG)){
					try{
						if(splitCommand.size() >= 2){
							String[] numbers = splitCommand.get(1).split(",");
							String result = sort(numbers);
							out.println(result);
						}else{
							out.println("Invalid number format!");
						}
					}catch(NumberFormatException e){
						out.println("Invalid input format!");
					}

				}else if(splitCommand.get(0).equalsIgnoreCase(MATRIX_MULTIPLICATION_TAG)){
					try{
						if(splitCommand.size() >= 2){
							String input = splitCommand.get(1);
							int[][] result = matrixMultiplication(input);
							ObjectTransfer obj = new ObjectTransfer();
							obj.setMatrix(result);
							ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
							oos.writeObject(obj);
//							oos.close();
						}else{
							out.println("Invalid number format!");
						}
					}catch(NumberFormatException e){
						out.println("Invalid input format!");
					}

				}else if(splitCommand.get(0).equalsIgnoreCase(FILESYNC_TAG)){
					if(splitCommand.size() >= 3){
						fileName = splitCommand.get(1);
						int fileSize = Integer.parseInt(splitCommand.get(2));
						out.print("Ok");
						ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
						ObjectTransfer obj = (ObjectTransfer) ois.readObject();
						if(fileUpload(fileName, obj.getBytes())){
							System.out.println(fileName+" synced with server successfully!");
							out.println(fileName+" synced with server successfully!");
						}
//						ois.close();
						// call file_list after this
					}else{
						System.out.println(FILESYNC_TAG + ": Invalid Command format!");
						out.println(FILESYNC_TAG + ": Invalid Command format!");
					}
					
				}else if(splitCommand.get(0).equalsIgnoreCase(STOP_TAG)){
					if(!clientSocket.isClosed())
						clientSocket.close();
					break;
				}

			}

		}catch(Exception e){
			e.printStackTrace();
			try {
				if(!serverSocket.isClosed())
					serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private byte[] fileDownload(String fileName){
		String filePath = SERVER_DIR + fileName;
		File file = new File(filePath);
		byte[] fileBytes = null;

		try {
			if(file.exists()){
				fileBytes = new byte[(int)file.length()];
				FileInputStream fip = new FileInputStream(file);
				while(fip.read(fileBytes) != -1 );
				fip.close();
				return fileBytes;
			}else{
				return null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return fileBytes;
	}
	
	private boolean fileUpload(String fileName, byte[] fileBytes){
		boolean result = false;
		String filePath = SERVER_DIR + fileName;
		File file = new File(filePath);
		FileOutputStream fos = null;

		try {
			if(file.exists())
				file.delete();
			fos = new FileOutputStream(file.getAbsolutePath());
			fos.write(fileBytes);
			if(file.exists()){
				return true;
			}else{
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	public boolean deleteFile(String fileName){
		boolean result = false;
		String filePath = SERVER_DIR + fileName;
		File file = new File(filePath);
		
		try {
			if(file.exists()){
				file.delete();
				return true;
			}else{
				System.out.println(DELETE_TAG + ": File to be deleted does not exist!");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean renameFile(String fileName, String newName){
		boolean result = false;
		String filePath = SERVER_DIR + fileName;
		File file = new File(filePath);
		try {
			if(file.exists()){
				file.renameTo(new File(SERVER_DIR + newName));
				return true;
			}else{
				System.out.println(RENAME_TAG + ": File to be renamed does not exist!");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String[] getFileList(){
		File directory = new File(SERVER_DIR);
		if(directory.isDirectory()){
			return directory.list();
		}else{
			return null;
		}
		
	}
	
	public float calculatePi(int n){
		// calculation based on Leibniz Formula For PI: pi = 4*(1 - 1/3 + 1/5 - 1/7 + 1/9 - 1/11 + ... + 1/2n-1 - 1/2n+1)
		float pi=0f;
		float seriesValue=0f;
		float denominator = 1f;
		for(float i=0; i < n; i++){
			if (i % 2 == 0) {
				seriesValue = seriesValue + (1f / denominator);
	         } else {
	        	seriesValue = seriesValue - (1f / denominator);
	         }
	         denominator = denominator + 2f;
		}
		pi = 4*seriesValue;
		return pi;
	}
	
	public String add(int x, int y){
		return String.valueOf(x+y);
	}
	
	public String sort(String[] numbers){
		int[] nums = new int[numbers.length];
		for(int i=0; i<numbers.length; i++){
			nums[i] = Integer.parseInt(numbers[i]);
		}
		Arrays.sort(nums);
		StringBuilder result = new StringBuilder("");
		for(int num : nums){
			result.append(String.valueOf(num) + " ");
		}
		return result.toString();
	}
	
	public int[][] matrixMultiplication(String input){
		ArrayList<int[][]> matrices = new ArrayList<>();
		String[] ip = input.split(";");
		for(String matrix : ip){
			String[] rows = matrix.split(",");
			int num_rows = rows.length;
			int k=0;
			int num_cols = rows[0].split(" ").length;
			int[][] array = new int[num_rows][num_cols];
			for (String row : rows){
				String[] columns = row.split(" ");
				for(int a=0; a < num_cols; a++){
					array[k][a] = Integer.parseInt(columns[a]);
				}
				k++;
			}
			matrices.add(array);
		}
		
		if(!matrices.isEmpty()){
			int r = matrices.get(0).length;
			int c = matrices.get(0)[0].length;
			int[][] result = new int[r][c];
			 
			for(int i=0; i < r; i++){
				for(int j=0; j < c; j++){
					result[i][j] = matrices.get(0)[i][j];
				}
			}
			
			for(int i=1; i < matrices.size(); i++){
				int r1 = result.length;
				int c1 = result[0].length;
				int r2 = matrices.get(i).length;
				int c2 = matrices.get(i)[0].length;
				int[][] mat2 = matrices.get(i);
				
				int[][] product = new int[r1][c2];
		        for(int p = 0; p < r1; p++) {
		            for (int q = 0; q < c2; q++) {
		                for (int x = 0; x < c1; x++) {
		                    product[p][q] += result[p][x] * mat2[x][q];
		                }
		            }
		        }
		        
		        result = product;
			}
			
			return result;
			
		}else{
			return null;
		}
	}
	

}

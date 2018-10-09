import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

public class ClientController {
	
	private int serverPort;
	private String serverAddress;
	private Socket clientSocket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	public static String response = "";
	private static final String CLIENT_DIR = ".\\ClientDirectory\\";
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
	
	private static HashMap<String, Long> fileState = new HashMap<>();
	
	public ClientController(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}
	
	public void initiateClient(){
		try {
			clientSocket = new Socket(serverAddress, serverPort);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			response = in.readLine();
			if(response.equalsIgnoreCase(ACK_TAG)){
				System.out.println("Connected to the server: "+clientSocket.getRemoteSocketAddress().toString());
				FTPClient.txt_result.setText("Connected to the server: "+clientSocket.getRemoteSocketAddress().toString());
				
				File homeDir = new File(CLIENT_DIR);
				if(!homeDir.exists())
					homeDir.mkdirs();
				for(File file : (new File(CLIENT_DIR)).listFiles()){
					fileState.put(file.getName(), file.lastModified());
				}
				
				new Thread(){
					public void run(){
						syncFiles();
					}
				}.start();
			}
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void downloadFile(String fileName){
		String response;
		String filePath = CLIENT_DIR + fileName;
		try {
			if(out != null && in != null){
				out.println(DOWNLOAD_TAG + ":"+fileName);
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				ObjectTransfer obj = (ObjectTransfer)ois.readObject();
				
				File file = new File(filePath);
				if(!file.exists()){
					FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
					fos.write(obj.getBytes());
					
					if(file.exists()){
						FTPClient.txt_result.setText(fileName + " downloaded successfully!");
					}else{
						FTPClient.txt_result.setText("Something went wrong..could not download "+fileName + "...");
					}
					
					fos.close();
				}
				
//				ois.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void uploadFile(String fileName, String filePath){
		String response;
		try{
			if(out != null && in != null){
				File file = new File(filePath);
				if(file.exists()){
					byte[] fileBytes = Files.readAllBytes(file.toPath());
					out.println(UPLOAD_TAG + ":" + fileName + ":" + file.length());
					response = in.readLine();
					FTPClient.txt_result.setText(response);
					if(response.equalsIgnoreCase("Ok")){
						ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
						ObjectTransfer obj = new ObjectTransfer();
						obj.setSize(fileBytes.length);
						obj.setBytes(fileBytes);
						oos.writeObject(obj);
//						oos.close();
						
						response = in.readLine();
					}
					
					FTPClient.txt_result.setText(response);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void listFiles(){
		String response;
		try{
			if(out != null && in != null){
				out.println(FILELIST_TAG);
				
				response = in.readLine();
				String[] temp;
				if(response.contains(":")){
					temp = response.split(":");
				}else{
					temp = new String[]{response};
				}
				
				DefaultTableModel tableModel = (DefaultTableModel)FTPClient.fileTable.getModel();
				tableModel.setRowCount(0);
				for(String str : temp){
					tableModel.addRow(new String[]{str});
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void deleteFile(String fileName){
		String response;
		try {
			if(out != null && in != null){
				out.println(DELETE_TAG + ":"+fileName);
				response = in.readLine();
				
				FTPClient.txt_result.setText(response);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void renameFile(String fileName, String newName){
		String response;
		try {
			if(out != null && in != null){
				out.println(RENAME_TAG + ":" + fileName + ":" + newName);
				response = in.readLine();
				
				FTPClient.txt_result.setText(response);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void disconnect(){
		if(out != null){
			out.println(STOP_TAG);
		}
		try {
			if(in != null){
				in.close();
				in = null;
			}
			if(out != null){
				out.close();
				out = null;
			}	
			if(!clientSocket.isClosed())
				clientSocket.close();
			FTPClient.txt_result.setText("Disconnected from server...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void calculatePi(){
		String response;
		try {
			if(out != null && in != null){
				out.println(CALCULATE_PI_TAG);
				response = in.readLine();
				
				FTPClient.txt_result.setText("Pi Value = "+response);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void add(String input){
		String response;
		try {
			if(out != null && in != null){
				String[] input1 = input.split(",");
				if(input1.length >= 2){
					int x = Integer.parseInt(input1[0]);		// just a number format check at client side itself
					int y = Integer.parseInt(input1[1]);
					out.println(ADD_TAG + ":" + String.valueOf(x)+ ":" + String.valueOf(y));
					response = in.readLine();

					FTPClient.txt_result.setText("Result of Add("+ String.valueOf(x)+ "," + String.valueOf(y)+") = "+response);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e){
		//	e.printStackTrace();
			FTPClient.txt_result.setText("Invalid number format!");
		}

	}
	
	public void sort(String numArray){
		String response;
		try {
			if(out != null && in != null){
				
				out.println(SORT_TAG + ":" + numArray);
				response = in.readLine();
				
				FTPClient.txt_result.setText("Result of Sort("+ numArray+") = "+response);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void matrixMultiplication(String input){
		try {
			if(out != null && in != null){
				
				out.println(MATRIX_MULTIPLICATION_TAG + ":" + input);
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				ObjectTransfer obj = (ObjectTransfer)ois.readObject();
				StringBuilder sb = new StringBuilder("\n");
				int[][] result = obj.getMatrix();
				for(int i=0; i < result.length; i++){
					for(int j=0; j < result[0].length; j++){
						sb.append(result[i][j]+" ");
					}
					sb.append('\n');
				}
				FTPClient.txt_result.setText("Result: "+sb.toString());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void syncFiles(){
		try {
			Socket syncSocket = new Socket(serverAddress, serverPort);
			PrintWriter out = new PrintWriter(syncSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(syncSocket.getInputStream()));
			response = in.readLine();

			if(response.equalsIgnoreCase(ACK_TAG)){
				System.out.println("Sync Service to the server: "+syncSocket.getRemoteSocketAddress().toString());
				//				FTPClient.txt_result.setText("Sync Service connected to the server: "+syncSocket.getRemoteSocketAddress().toString());
			}
			while(true){
				File[] filelist = (new File(CLIENT_DIR)).listFiles();
				for(File file : filelist){

					if(!fileState.containsKey(file.getName()) || file.lastModified() > fileState.get(file.getName())){
						if(out != null && in != null){
							byte[] fileBytes = Files.readAllBytes(file.toPath());
							String byteString = new String(fileBytes,"UTF-8");
							out.println(FILESYNC_TAG + ":" + file.getName() + ":" + file.length());
							ObjectTransfer obj = new ObjectTransfer();
							obj.setSize(fileBytes.length);
							obj.setBytes(fileBytes);
							ObjectOutputStream oos = new ObjectOutputStream(syncSocket.getOutputStream());
							oos.writeObject(obj);
							
//							oos.close();

							response = in.readLine();

							FTPClient.txt_result.setText(response);
							
							fileState.put(file.getName(), System.currentTimeMillis());

						}
					}
				}

				Thread.sleep(10000);	// Sync every 10 sec
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public byte[] getFullBuffer(int size) throws IOException{
		char[] buffer = new char[size];
		int offset = 0;
		int bytes_read = 0;
		if(in != null){
			while(offset < size && bytes_read != -1){
				bytes_read = in.read(buffer, offset, (size-offset));
				offset += bytes_read;
			}
			return (new String(buffer)).getBytes("UTF-8");
		}else{
			return null;
		}
	}
	
	
}

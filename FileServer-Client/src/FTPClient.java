import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FTPClient extends JFrame implements WindowListener{
	
	public static JTable fileTable;
	private String[] columns = {"Files"};
	private String[][] data = {{""}};
	private JLabel lbl_addr;
	private JLabel lbl_port;
	private JLabel lbl_input;
	private JTextField txt_addr;
	private JTextField txt_port;
	private JButton bttn_connect;
	private JButton bttn_download;
	private JButton bttn_upload;
	private JButton bttn_delete;
	private JButton bttn_rename;
	private JButton bttn_refresh;
	private JButton bttn_disconnect;
	private JButton bttn_calcPi;
	private JButton bttn_add;
	private JButton bttn_sort;
	private JButton bttn_matMultiply;
	public static JTextArea txt_result;
	private final JFileChooser fileChooser = new JFileChooser();
	
	private static ClientController controller = null;
	
	public FTPClient(){
		init();
	}

	public static void main(String[] args) {
		
		FTPClient client = new FTPClient();
		client.setVisible(true);
	}
	
	
	
	private void init(){
		setTitle("FTP Client");
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        lbl_addr  = new JLabel("Server Address: ");
        txt_addr = new JTextField("Enter Address");
        txt_addr.setEditable(true);
        lbl_port  = new JLabel("Port: ");
        txt_port = new JTextField("Enter Port");
        txt_port.setEditable(true);
        bttn_connect = new JButton("Connect");
        JPanel jp1 = new JPanel();
        jp1.add(lbl_addr);
        jp1.add(txt_addr);
        jp1.add(lbl_port);
        jp1.add(txt_port);
        jp1.add(bttn_connect);
        
        fileTable = new JTable(new DefaultTableModel(data, columns));
        
        JScrollPane scrollPane = new JScrollPane(fileTable);
        fileTable.setFillsViewportHeight(false);
        
        JPanel top_panel = new JPanel();
        top_panel.add(jp1, BorderLayout.NORTH);
        top_panel.add(scrollPane, BorderLayout.CENTER);
        
        this.add(top_panel, BorderLayout.NORTH);
        
        bttn_download = new JButton("Download");
        bttn_upload = new JButton("Upload");
        bttn_delete = new JButton("Delete");
        bttn_rename = new JButton("Rename");
        bttn_refresh = new JButton("Refresh");
        bttn_disconnect = new JButton("Disconnect");
        
        JPanel jp2 = new JPanel();
        jp2.add(bttn_download);
        jp2.add(bttn_upload);
        jp2.add(bttn_delete);
        jp2.add(bttn_rename);
        jp2.add(bttn_refresh);
        jp2.add(bttn_disconnect);
        
        bttn_calcPi = new JButton("CALCULATE_PI ()");
    	bttn_add = new JButton("ADD (i,j)");
    	bttn_sort = new JButton("SORT (arrayA)");
    	bttn_matMultiply = new JButton("MATRIX_MULTIPLY (matA, matB, matC)");
        
        JPanel jp3 = new JPanel();
        jp3.add(bttn_calcPi);
        jp3.add(bttn_add);
        jp3.add(bttn_sort);
        jp3.add(bttn_matMultiply);
        
        JPanel middle_panel = new JPanel();
        middle_panel.add(jp2, BorderLayout.NORTH);
        middle_panel.add(jp3, BorderLayout.CENTER);
        
        this.add(middle_panel, BorderLayout.CENTER);
        
        txt_result = new JTextArea("Enter your Parameters for RPC here:");
        txt_result.setColumns(40);
        txt_result.setRows(5);
        txt_result.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        lbl_input = new JLabel("Input: ");
        
        JPanel bottom_panel = new JPanel();
        bottom_panel.add(lbl_input, BorderLayout.NORTH);
        bottom_panel.add(txt_result, BorderLayout.SOUTH);
        
        this.add(bottom_panel, BorderLayout.SOUTH);
        
        bttn_connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller = new ClientController(txt_addr.getText(), Integer.parseInt(txt_port.getText()));
				controller.initiateClient();
				controller.listFiles();
			}
		});
        
        bttn_download.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					int selectedRow = fileTable.getSelectedRow();
					if(selectedRow != -1){
						controller.downloadFile((String)fileTable.getValueAt(selectedRow, 0));
					}
				}
				
			}
		});
        
        bttn_upload.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					int returnValue = fileChooser.showOpenDialog(bttn_upload);
					if(returnValue == JFileChooser.APPROVE_OPTION){
						File file = fileChooser.getSelectedFile();
						controller.uploadFile(file.getName(),file.getAbsolutePath());
					}else{
						System.out.println("Upload: User did not choose any file");
					}
				}
				
			}
		});
        
        bttn_disconnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					controller.disconnect();
				}
			}
		});
        
        bttn_refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					controller.listFiles();
				}
				
			}
		});
        
        bttn_delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					int selectedRow = fileTable.getSelectedRow();
					if(selectedRow != -1){
						controller.deleteFile((String)fileTable.getValueAt(selectedRow, 0));
					}
				}
				
			}
		});
        
        bttn_rename.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					int selectedRow = fileTable.getSelectedRow();
					if(selectedRow != -1){
						String fileName = (String)fileTable.getValueAt(selectedRow, 0);
						String newName = JOptionPane.showInputDialog("Rename "+fileName+" with: ");
						controller.renameFile(fileName, newName);
					}
				}
				
			}
		});
        
        bttn_calcPi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					String input = txt_result.getText();
					controller.calculatePi();
				}
				
			}
		});
        
        bttn_add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					controller.add(txt_result.getText());
				}
			}
		});
        
        bttn_sort.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					controller.sort(txt_result.getText());
				}
			}
		});
        
        bttn_matMultiply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller != null){
					controller.matrixMultiplication(txt_result.getText());
				}
			}
		});
        
        
        bttn_add.setToolTipText("Type 2 numbers (comma separated) in the text area below in this format: number1,number2    ex. 4,5");
        bttn_sort.setToolTipText("Give numbers (comma separated) in the text area below in this format: num1,num2,num3,..    ex. 2,1,4,5");
        bttn_matMultiply.setToolTipText("Elements in each row should be space separated..rows comma separated...and matrices semicolon(;) separatd.");
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {
		if(controller != null){
			controller.disconnect();
		}
		System.out.println("Successfully disconnected from Server!");
//		System.exit(0);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if(controller != null){
			controller.disconnect();
		}
		System.out.println("Successfully disconnected from Server!");
//		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

}

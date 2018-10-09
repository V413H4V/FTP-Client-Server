import java.io.Serializable;

public class ObjectTransfer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1001626620L;
	private int size;
	private byte[] bytes;
	private String command;
	private int[][] matrix;
	
	public int[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	

}

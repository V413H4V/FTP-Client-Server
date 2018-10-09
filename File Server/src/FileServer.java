import java.io.IOException;

public class FileServer {

	public static void main(String[] args) {

		int portNumber = Integer.parseInt(args[0]);
		String mode = args[1];
		
		if(mode.equalsIgnoreCase("SingleThread")){
			SingleThreadedServer server = new SingleThreadedServer(portNumber);
			try {
				server.startServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(mode.equalsIgnoreCase("MultiThread")){
			MultiThreadedServer server = new MultiThreadedServer(portNumber);
			try {
				server.initiateServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}

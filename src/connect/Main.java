package connect;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NodeConnect api = new NodeConnect("http://192.168.0.6:8080");
		api.contactMainServer();
		
		OpenSocket socket = new OpenSocket();
		socket.openServer(5555);
		
		api.contactNode();
	}

}

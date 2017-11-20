package connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class OpenSocket {
	public OpenSocket() {
		// Empty Constructor
	}

	public void connClient(String ip, int port) {
		try {
			Socket mSocket = new Socket(ip, port);
			BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			PrintWriter writer = new PrintWriter(mSocket.getOutputStream());
			writer.println("Access to : " + mSocket.getInetAddress());
			writer.flush();
			mSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void openServer(int port) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ServerSocket mServerSocket = new ServerSocket(port);
					System.out.println("Server Start!!");
					while(true) {
					Socket mSocket = mServerSocket.accept();
					BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
					PrintWriter writer = new PrintWriter(mSocket.getOutputStream());
					System.out.println(reader.readLine());
					writer.println("Hello");
					writer.flush();
					mSocket.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		th.start();
	}
}

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import properties.Properties;

public class Server {
	private int port;
	private ServerSocket server;
	private ArrayList<Socket> clients;
	private Integer n;
	
	public static void main(String[] args) throws IOException {
		Map<String, String> properties = new Properties("../config.properties").get();
		
		new Server(Integer.parseInt(properties.get("serverPort")), 
				Integer.parseInt(properties.get("clientsNumber"))).execute();
	}
	
	public Server(int port, Integer n) throws IOException {
		this.port = port;
		this.clients = new ArrayList<Socket>();
		this.n = n;
	}
	
	public void execute() throws IOException {
		server = new ServerSocket(this.port);
		System.out.println("Porta " + this.port + " aberta!");
		
		for(int i = 0; i < this.n; i++) {
			this.clients.add(server.accept());
			System.out.println(i+1 + " conectado");
		}
		
		for(int i = 0; i < this.n; i++) {
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.clients.get(i).getOutputStream()));
			out.flush();
			
			Message msg = new Message();
			msg.setValue("this is my message and thats all!!!");
			
			out.writeObject(msg);
			
			this.clients.get(i).close();
		}
		
	}
}
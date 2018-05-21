import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import properties.Properties;

public class ClienteFilosofo {
	private Socket socket;
	private String server;
	private int port;

	public static void main(String[] args) throws IOException, NumberFormatException, ClassNotFoundException {
		Map<String, String> properties = new Properties("../config.properties").get();
		
		new ClienteFilosofo(" ", properties.get("serverInit"), 
				Integer.parseInt(properties.get("serverPort")) 
				).waitInit();
		
//		new ClienteFilosofo(" ", properties.get("serverNeigh"), 
//				Integer.parseInt(properties.get("serverPort")) 
//				).execute();
	}
	
	public ClienteFilosofo(String client, String server, int port) throws UnknownHostException, IOException {
		this.port = port;
		this.server = server;
		this.socket = new Socket(this.server, this.port);
	}

	public void waitInit() throws IOException, ClassNotFoundException{
		System.out.println("esperando");
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		Message msg = (Message) in.readObject();
		System.out.println("recebido" + msg.getValue());
		
		this.socket.close();
	}
	
	public void execute() throws IOException, ClassNotFoundException{
		Map<String, String> properties = new Properties("../config.properties").get();
		
		System.out.println("Conectando ao filosofo " + properties.get("serverNeigh"));
		
//		while(!true);
		
//		new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		
//		System.out.println("recebido");
		
		this.socket.close();
	}
}

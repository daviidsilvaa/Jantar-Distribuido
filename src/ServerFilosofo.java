import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import properties.Properties;

public class ServerFilosofo implements Runnable{
	private int port;
	private ServerSocket server;
	private Socket socket;
	private boolean isStopped;

	private boolean myHashi;
	private boolean neighHashi;

	public void run(){
		System.out.println("Porta " + this.port + " aberta!");
		try{
			this.socket = this.server.accept();
		}catch(Exception e){}
	}

	public ServerFilosofo(int port) throws IOException {
		this.port = port;
		this.server = new ServerSocket(this.port);
		this.myHashi = false;
		this.neighHashi = false;
		this.isStopped = false;
	}

	public void execute() throws IOException {
		//while(!this.isStopped) {
		//	new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		//}
	}
}

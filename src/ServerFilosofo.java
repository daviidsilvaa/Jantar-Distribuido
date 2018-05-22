import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		while(!this.isStopped) {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			Message msg = new Message();
			
			try{
				msg = (Message) in.readObject();
			}catch(Exception e){
				e.printStackTrace();;
			}
			
			if(msg.getValue() == "pedindoHashi") {
				if(this.myHashi == false) {
					msg.setValue("disponivel");
				} else {
					msg.setValue("indisponivel");
				}
			}
			
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
			
			out.writeObject(msg);
			out.flush();
		}
	}
}

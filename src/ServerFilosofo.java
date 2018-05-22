import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFilosofo implements Runnable{
	private int port;
	private ServerSocket server;
	private Socket socket;
	private boolean isStopped;

	private boolean myHashi;

	public void run(){
		System.out.println("Porta " + this.port + " aberta!");
		try{
			this.socket = this.server.accept();
			this.execute();
		}catch(Exception e){}
	}

	public ServerFilosofo(int port) throws IOException {
		this.port = port;
		this.server = new ServerSocket(this.port);
		this.myHashi = false;
		this.isStopped = false;
	}

	public void execute() throws IOException {
		while(!this.isStopped) {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			Message msg = new Message();
			
			try{
				msg = (Message) in.readObject();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(msg.getValue() == "pedindoHashi") {
				if(this.myHashi == false) {
					this.myHashi = true;
					msg.setValue("disponivel");
				} else {
					msg.setValue("indisponivel");
				}
				
				ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
				
				out.writeObject(msg);
				out.flush();
			}
			
			if(msg.getValue() == "devolvendoHashi") {
				this.myHashi = false;
			}
		}
	}
}

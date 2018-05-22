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

	private boolean isHashiInUse;

	public void run(){
		System.out.println("Porta " + this.port + " aberta!");
		while(!this.isStopped) {
			try{
				this.socket = this.server.accept();
				this.execute();
			}catch(Exception e){}
		}
	}

	public ServerFilosofo(int port) throws IOException {
		this.port = port;
		this.server = new ServerSocket(this.port);
		this.isHashiInUse = false;
		this.isStopped = false;
	}

	public void execute() throws IOException {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		Message msg = new Message();

		try{
			msg = (Message) in.readObject();
		}catch(Exception e){
			e.printStackTrace();
		}

		if(msg.getValue().equals("pedindoHashi")) {
			if(this.isHashiInUse == false) {
				msg.setValue("disponivel");
				this.isHashiInUse = true;
			} else {
				msg.setValue("indisponivel");
				System.out.println("indisponivel");
			}

			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));

			out.writeObject(msg);
			out.flush();
		}

		if(msg.getValue().equals("devolvendoHashi")) {
			this.isHashiInUse = false;
		}

		if(msg.getValue().equals("pegandoHashi")) {
			this.isHashiInUse = true;
		}
	}
}

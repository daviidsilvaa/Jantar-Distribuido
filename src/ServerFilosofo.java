import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import properties.Properties;

public class ServerFilosofo implements Runnable{
	private int port;
	private ServerSocket server;
	private Socket socket;
	private boolean isStopped;
	private Queue<String> list;
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
		this.list = new LinkedList<String>();
	}

	public void execute() throws IOException {
		Map<String, String> properties = new Properties("../config.properties").get();
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
				list.add(msg.getIp());
			}

			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));

			out.writeObject(msg);
			out.flush();
		}

		if(msg.getValue().equals("devolvendoHashi")) {
			this.isHashiInUse = false;
			
			@SuppressWarnings("resource")
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
					new Socket(this.list.poll(), Integer.parseInt(properties.get("philoPort"))).getOutputStream()));
			
			msg.setValue("enfim disponivel");
			out.writeObject(msg);
			out.flush();
		}

		if(msg.getValue().equals("pegandoHashi")) {
			this.isHashiInUse = true;
		}
		
		if (msg.getValue().equals("fecharServer")) {
			try{
				TimeUnit.SECONDS.sleep(3);
			}catch(Exception e){}
			this.isStopped = true;
		}
	}
}

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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

		new Thread(new ServerFilosofo(Integer.parseInt(properties.get("philoPort")))).start();

		try{
			TimeUnit.SECONDS.sleep(1);
		}catch(Exception e){}

		new ClienteFilosofo(" ", properties.get("serverNeigh"),
				Integer.parseInt(properties.get("philoPort"))
				).execute();
	}

	public ClienteFilosofo(String client, String server, int port) throws UnknownHostException, IOException {
		this.port = port;
		this.server = server;
		this.socket = new Socket(this.server, this.port);
	}

	public void waitInit() throws IOException, ClassNotFoundException{
		System.out.println("esperando");

		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		Message msg = new Message();
		try{
			msg = (Message) in.readObject();
		}catch(Exception e){
			System.out.println("Erro");
		}
		System.out.println("recebido: " + msg.getValue());

		in.close();
		this.socket.close();
	}

	public void execute() throws IOException, ClassNotFoundException{
		Map<String, String> properties = new Properties("../config.properties").get();

		System.out.println("Conectado ao filosofo " + properties.get("serverNeigh"));

		//while(true) {
			sleep();
			think();
			eat();
			//			System.out.println("recebido");
		//}
	}

	public void sleep() {
		Random rand = new Random();
		try{
			TimeUnit.MILLISECONDS.sleep(rand.nextInt(50));
			System.out.println("dormindo");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void think() {
		Random rand = new Random();
		try{
			TimeUnit.MILLISECONDS.sleep(rand.nextInt(50));
			System.out.println("pensando");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void eat() throws IOException {
		Map<String, String> properties = new Properties("../config.properties").get();
		
		boolean hashi1 = this.requestHashi("localhost");
		boolean hashi2 = this.requestHashi(properties.get("serverNeigh"));
		
		System.out.println(hashi1 + " : " + hashi2);
	}
	
	public boolean requestHashi(String ip) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
		Message msg = new Message("pedindoHashi");
		out.writeObject(msg);
		out.flush();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		try{
			msg = (Message) in.readObject();
		}catch(Exception e){
			e.printStackTrace();;
		}
		
		if(msg.getValue() == "disponivel") {
			return true;
		}
		
		return false;
	}
}

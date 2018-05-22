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

		ClienteFilosofo c1 = new ClienteFilosofo(properties.get("serverInit"),
				Integer.parseInt(properties.get("serverPort"))
				);
		c1.waitInit();

		new Thread(new ServerFilosofo(Integer.parseInt(properties.get("philoPort")))).start();

		try{
			TimeUnit.SECONDS.sleep(1);
		}catch(Exception e){}

		ClienteFilosofo c2 = new ClienteFilosofo(properties.get("serverNeigh"),
				Integer.parseInt(properties.get("philoPort"))
				);
		c2.execute();
	}

	public ClienteFilosofo(String server, int port) throws UnknownHostException, IOException {
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
		System.out.println("Conectado ao filosofo " + this.server);

		//while(true) {
		sleep();
		think();
		eat();
		//			System.out.println("recebido");
		//}
	}

	public void sleep() {
		System.out.println("dormindo");
		sleep1(50);
	}

	public void think() {
		System.out.println("pensando");
		sleep1(50);
	}

	public void eat() throws IOException {
		Map<String, String> properties = new Properties("../config.properties").get();

		boolean hashi1 = this.requestHashi("localhost");
		boolean hashi2 = this.requestHashi(properties.get("serverNeigh"));

		if(!hashi1 && !hashi2) {
			System.out.println("comeu");
			sleep1(100);
			returnHashi("localhost");
			returnHashi(properties.get("serverNeigh"));
		} else {
			System.out.println("nao comeu");
		}

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
	
	public void returnHashi(String ip) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
		Message msg = new Message("devolvendoHashi");
		out.writeObject(msg);
		out.flush();
	}

	public void sleep1(int t) {
		Random rand = new Random();
		try{
			TimeUnit.MILLISECONDS.sleep(rand.nextInt(t));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

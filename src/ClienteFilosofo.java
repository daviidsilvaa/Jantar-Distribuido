import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import properties.Properties;

public class ClienteFilosofo {
	private Socket mySocket;
	private Socket neighSocket;
	private String server;

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
				Integer.parseInt(properties.get("philoPort")));
		c2.execute();
	}

	public ClienteFilosofo(String server, int port) throws UnknownHostException, IOException {
		this.server = server;
	}
	
	public void waitInit() throws IOException, ClassNotFoundException{
		System.out.println("esperando");

		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(neighSocket.getInputStream()));
		Message msg = new Message();
		try{
			msg = (Message) in.readObject();
		}catch(Exception e){
			System.out.println("Erro");
		}
		System.out.println("recebido: " + msg.getValue());

		in.close();
		this.neighSocket.close();
	}

	public void execute() throws IOException, ClassNotFoundException{
		System.out.println("Conectado ao filosofo " + this.server);

		while(true) {
			sleep();
			think();
			eat();
		}
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

		boolean trying = true;
		boolean hashi1 = false;
		boolean hashi2 = false;

		while(trying) {
			System.out.println("tentando comer");
			Random rand = new Random();
			if(rand.nextBoolean() == true) {
				
				hashi1 = this.requestHashi("localhost");

				if(hashi1 == false) {
					System.out.println("\tpegou hashi 1 " + LocalDateTime.now().toLocalTime());
					hashi1 = true;

					hashi2 = this.requestHashi(properties.get("serverNeigh"));

					if(hashi2 == false) {
						System.out.println("\tpegou hashi 2 " + LocalDateTime.now().toLocalTime());
						hashi2 = true;
						trying = false;
						
						//sleep1(2000);
						try{
							TimeUnit.SECONDS.sleep(2);
						}catch(Exception e){
							e.printStackTrace();
						}
						returnHashi(properties.get("serverNeigh"));
						System.out.println("\tentregou hashi 2 " + LocalDateTime.now().toLocalTime());
						returnHashi("localhost");
						System.out.println("\tentregou hashi 1 " + LocalDateTime.now().toLocalTime());
					} else {
						System.out.println("\tnao pegou hashi 2 " + LocalDateTime.now().toLocalTime());
						returnHashi("localhost");
						System.out.println("\tentregou hashi 2 " + LocalDateTime.now().toLocalTime());
					}
				} else System.out.println("\\tnao pegou hashi 1 " + LocalDateTime.now().toLocalTime());
			} else {
				hashi2 = this.requestHashi(properties.get("serverNeigh"));

				if(hashi2 == false) {
					System.out.println("\tpegou hashi 2 " + LocalDateTime.now().toLocalTime());
					hashi2 = true;
					
					hashi1 = this.requestHashi("localhost");

					if(hashi1 == false) {
						System.out.println("\tpegou hashi 1 " + LocalDateTime.now().toLocalTime());
						hashi1 = true;
						
						trying = false;
						//sleep1(2000);
						try{
							TimeUnit.SECONDS.sleep(2);
						}catch(Exception e){
							e.printStackTrace();
						}
						returnHashi(properties.get("serverNeigh"));
						System.out.println("\tentregou hashi 2 " + LocalDateTime.now().toLocalTime());
						returnHashi("localhost");
						System.out.println("\tentregou hashi 1 " + LocalDateTime.now().toLocalTime());
					} else {
						System.out.println("\tnao pegou hashi 1 " + LocalDateTime.now().toLocalTime());
						returnHashi(properties.get("serverNeigh"));
						System.out.println("\tentregou hashi 2 " + LocalDateTime.now().toLocalTime());
					}
				} else System.out.println("\\tnao pegou hashi 2 " + LocalDateTime.now().toLocalTime());
			}
			trying = rand.nextBoolean();
		}
	}

	public boolean requestHashi(String ip) throws IOException {
		Map<String, String> properties = new Properties("../config.properties").get();
		ObjectOutputStream out;
		ObjectInputStream in;
		
		if(ip.equals("localhost")) {
			this.mySocket = new Socket("localhost", 
					Integer.parseInt(properties.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		}
			
		else {
			this.neighSocket = new Socket(properties.get("serverNeigh"), 
					Integer.parseInt(properties.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.neighSocket.getOutputStream()));
		}
			
		
		Message msg = new Message("pedindoHashi");
		out.writeObject(msg);
		out.flush();

		if(ip.equals("localhost"))
			in = new ObjectInputStream(new BufferedInputStream(this.mySocket.getInputStream()));
		else
			in = new ObjectInputStream(new BufferedInputStream(this.neighSocket.getInputStream()));
		
		try{
			msg = (Message) in.readObject();
		}catch(Exception e){
			e.printStackTrace();;
		}

		this.mySocket.close();
		this.neighSocket.close();
		
		if(msg.getValue().equals("disponivel")) {
			return false;
		}
		
		return true;
	}

	public void returnHashi(String ip) throws IOException {
		Map<String, String> properties = new Properties("../config.properties").get();
		ObjectOutputStream out;
		
		if(ip.equals("localhost")) {
			this.mySocket = new Socket("localhost", 
					Integer.parseInt(properties.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		}
			
		else {
			this.neighSocket = new Socket(properties.get("serverNeigh"), 
					Integer.parseInt(properties.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.neighSocket.getOutputStream()));
		}
		
		Message msg = new Message("devolvendoHashi");
		out.writeObject(msg);
		out.flush();
		
		this.mySocket.close();
		this.neighSocket.close();
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

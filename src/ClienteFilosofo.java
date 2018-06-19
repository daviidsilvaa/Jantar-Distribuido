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
	private Socket mySocket;
	private Socket neighSocket;
	private String server;
	private Integer eat_t;
	private boolean tryEat = true;
	private Map<String, String> prop;

	public static void main(String[] args) throws IOException, NumberFormatException, ClassNotFoundException {
		Map<String, String> properties = new Properties("../config.properties").get();

		ClienteFilosofo c1 = new ClienteFilosofo(properties.get("serverInit"),
				Integer.parseInt(properties.get("serverPort")), "../config.properties");
		c1.waitInit();

		new Thread(new ServerFilosofo(Integer.parseInt(properties.get("philoPort")))).start();

		try{
			TimeUnit.SECONDS.sleep(1);
		}catch(Exception e){}

		ClienteFilosofo c2 = new ClienteFilosofo(properties.get("serverNeigh"),
				Integer.parseInt(properties.get("philoPort")), "../config.properties");
		c2.execute();
	}

	public ClienteFilosofo(String server, int port, String prop_path) throws UnknownHostException, IOException {
		this.eat_t = 0;
		this.server = server;
		this.prop = new Properties("../config.properties").get();
	}

	public ClienteFilosofo(String server, int port) throws UnknownHostException, IOException {
		this.eat_t = 0;
		this.server = server;
	}

	public void waitInit() throws IOException, ClassNotFoundException{
		System.out.println("esperando");

		this.neighSocket = new Socket(this.prop.get("serverInit"), Integer.parseInt(this.prop.get("serverPort")));

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
		long start = System.nanoTime();
		long now = 0;

		while((now - start)/1000000000 < 10) {
			Random rand = new Random();
			switch(rand.nextInt(3)) {
			case 0: sleep();
			break;
			case 1: think();
			break;
			case 2:
				if (this.tryEat)
					eat();
				break;
			}
			now = System.nanoTime();
		}
		System.out.println("filosofo comeu " + this.eat_t + " vezes");
		sleep1(3000);
		closeServer();
	}

	private void closeServer() throws IOException {
		ObjectOutputStream out;
		this.mySocket = new Socket("localhost",
				Integer.parseInt(this.prop.get("philoPort")));
		out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		Message msg = new Message("fecharServer", new LocalIP().get());
		out.writeObject(msg);
		out.flush();
	}

	public void sleep() {
		//		System.out.println("dormindo");
		sleep1(100);
	}

	public void think() {
		//		System.out.println("pensando");
		sleep1(100);
	}

	public void eat() throws IOException {
		String who = null;
		boolean hashi1 = false;
		boolean hashi2 = false;

//		System.out.println("tentando comer");
		Random rand = new Random();
		if(rand.nextBoolean() == true) {

			hashi1 = this.requestHashi("localhost");

			if(hashi1 == false) {
//				System.out.println("\tpegou hashi 1 " + LocalDateTime.now().toLocalTime());
				hashi1 = true;

				hashi2 = this.requestHashi(this.prop.get("serverNeigh"));

				if(hashi2 == false) {
//					System.out.println("\tpegou hashi 2 " + LocalDateTime.now().toLocalTime());
					hashi2 = true;

					sleep1(1200);

					returnHashi(this.prop.get("serverNeigh"));
//					System.out.println("\tentregou hashi 2 " + LocalDateTime.now().toLocalTime());
					returnHashi("localhost");
//					System.out.println("\tentregou hashi 1 " + LocalDateTime.now().toLocalTime());
					this.eat_t++;
				} else {
//					System.out.println("\tnao pegou hashi 2 " + LocalDateTime.now().toLocalTime());
					returnHashi("localhost");
					this.tryEat = false;
					who = "serverNeigh";
//					System.out.println("\tentregou hashi 2 " + LocalDateTime.now().toLocalTime());
				}
			} else {
//				se o hashi nao estiver disponivel
				this.tryEat = false;
				who = "localhost";
			}
//				else System.out.println("\tnao pegou hashi 1 " + LocalDateTime.now().toLocalTime());
		} else {
			hashi2 = this.requestHashi(this.prop.get("serverNeigh"));

			if(hashi2 == false) {
//				System.out.println("\tpegou hashi 2 " + LocalDateTime.now().toLocalTime());
				hashi2 = true;

				hashi1 = this.requestHashi("localhost");

				if(hashi1 == false) {
//					System.out.println("\tpegou hashi 1 " + LocalDateTime.now().toLocalTime());
					hashi1 = true;

					sleep1(1200);

					returnHashi(this.prop.get("serverNeigh"));
//					System.out.println("\tentregou hashi 2 " + LocalDateTime.now().toLocalTime());
					returnHashi("localhost");
//					System.out.println("\tentregou hashi 1 " + LocalDateTime.now().toLocalTime());
					this.eat_t++;
				} else {
//					System.out.println("\tnao pegou hashi 1 " + LocalDateTime.now().toLocalTime());
					returnHashi(this.prop.get("serverNeigh"));
					this.tryEat = false;
					who = "localhost";
//					System.out.println("\tentregou hashi 2 " + LocalDateTime.now().toLocalTime());
				}
			} else{
				this.tryEat = false;
				who = "serverNeigh";
			}
//				else System.out.println("\tnao pegou hashi 2 " + LocalDateTime.now().toLocalTime());
		}
		
		if(this.tryEat == false)
//			new Thread(this.foo(who)).start();;
			waitingHashi(who);
	}

	public void waitingHashi(String ip) throws IOException {
		ObjectOutputStream out;
		ObjectInputStream in;

		if(ip.equals("localhost")) {
			this.mySocket = new Socket("localhost",
					Integer.parseInt(this.prop.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		}
		else {
			this.neighSocket = new Socket(this.prop.get("serverNeigh"),
					Integer.parseInt(this.prop.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.neighSocket.getOutputStream()));
		}


		Message msg = new Message("esperandoHashi", new LocalIP().get());
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

		if(msg.getValue().equals("enfim disponivel")) {
			this.tryEat = true;
		}
	}
	
	public boolean requestHashi(String ip) throws IOException {
		ObjectOutputStream out;
		ObjectInputStream in;

		if(ip.equals("localhost")) {
			this.mySocket = new Socket("localhost",
					Integer.parseInt(this.prop.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		}
		else {
			this.neighSocket = new Socket(this.prop.get("serverNeigh"),
					Integer.parseInt(this.prop.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.neighSocket.getOutputStream()));
		}


		Message msg = new Message("pedindoHashi", new LocalIP().get());
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

		if(msg.getValue().equals("disponivel")) {
			return false;
		}

		return true;
	}

	public void returnHashi(String ip) throws IOException {
		ObjectOutputStream out;

		if(ip.equals("localhost")) {
			this.mySocket = new Socket("localhost",
					Integer.parseInt(this.prop.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		}

		else {
			this.neighSocket = new Socket(this.prop.get("serverNeigh"),
					Integer.parseInt(this.prop.get("philoPort")));
			out = new ObjectOutputStream(new BufferedOutputStream(this.neighSocket.getOutputStream()));
		}

		Message msg = new Message("devolvendoHashi", new LocalIP().get());
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

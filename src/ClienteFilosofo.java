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
		this.neighSocket = new Socket(this.server, this.port);
		this.mySocket = new Socket("localhost", this.port);
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
					System.out.println("\tpegou hashi 1");
					takeHashi("localhost");
					hashi1 = true;

					hashi2 = this.requestHashi(properties.get("serverNeigh"));

					if(hashi2 == false) {
						System.out.println("\tpegou hashi 2");
						takeHashi(properties.get("serverNeigh"));
						hashi2 = true;
						
						trying = false;
						System.out.println("comeu");
						sleep1(100);
						returnHashi(properties.get("serverNeigh"));
						returnHashi("localhost");
					} else {
						System.out.println("\tnao pegou hashi 2");
						returnHashi("localhost");
					}
				}
			} else {
				hashi2 = this.requestHashi(properties.get("serverNeigh"));

				if(hashi2 == false) {
					System.out.println("\tpegou hashi 2");
					takeHashi(properties.get("serverNeigh"));
					hashi2 = true;
					
					hashi1 = this.requestHashi("localhost");

					if(hashi1 == false) {
						System.out.println("\tpegou hashi 1");
						takeHashi("localhost");
						hashi1 = true;
						
						trying = false;
						System.out.println("comeu");
						sleep1(100);
						returnHashi(properties.get("serverNeigh"));
						returnHashi("localhost");						
					} else {
						System.out.println("\tnao pegou hashi 1");
						returnHashi(properties.get("serverNeigh"));
					}
				}
			}
			trying = rand.nextBoolean();
		}

		//		while(TRUE)
		//	    {
		//	         /* thinking section */
		//	         trying = TRUE;
		//	         while(trying)
		//	         {
		//	              choose side randomly and uniformly from {0,1};
		//	              otherside = complement of side;
		//	              wait until (forkkAvailable[i-side] is TRUE and change it to FALSE);
		//	              if (forkAvailable[i-otherside] is TRUE and change it to FALSE)
		//	                 then trying = FALSE;
		//	                 else  forkAvailable[i-side] = TRUE;
		//	         }
		//	          /* eating section */
		//	         forkAvailable[i-1] = forkAvailable[i] = TRUE;
		//	     }

	}

	public void takeHashi(String ip) throws IOException {
		ObjectOutputStream out;
		
		if(ip.equals("localhost"))
			out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		else
			out = new ObjectOutputStream(new BufferedOutputStream(this.neighSocket.getOutputStream()));
		Message msg = new Message("pegandoHashi");
		out.writeObject(msg);
		out.flush();
	}

	public boolean requestHashi(String ip) throws IOException {
		ObjectOutputStream out;
		ObjectInputStream in;
		
		if(ip.equals("localhost"))
			out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		else
			out = new ObjectOutputStream(new BufferedOutputStream(this.neighSocket.getOutputStream()));
		
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

		if(msg.getValue().equals("disponivel")) {
			return false;
		}
		
		return true;
	}

	public void returnHashi(String ip) throws IOException {
		ObjectOutputStream out;
		
		if(ip.equals("localhost"))
			out = new ObjectOutputStream(new BufferedOutputStream(this.mySocket.getOutputStream()));
		else
			out = new ObjectOutputStream(new BufferedOutputStream(this.neighSocket.getOutputStream()));
		
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

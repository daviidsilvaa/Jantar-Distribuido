import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private String value;
	private String ip;

	public Message(String v) {
		this.value = v;
	}
	
	public Message(String v, String ip){
		this.value = v;
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Message(){
	}

	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}

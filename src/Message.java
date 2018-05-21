import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private String value;
	
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}

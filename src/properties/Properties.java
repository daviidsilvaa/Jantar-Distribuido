package properties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Properties {
	private String path;
	
	public Properties(String path) {
		this.path = path;
	}
	
	@SuppressWarnings("resource")
	public HashMap<String, String> get() throws IOException{
		Map<String, String> env = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(this.path));
		String line;
		while ((line = br.readLine()) != null) {
			String[] line_split = new String[2];
			line_split = line.split(" = ");
			env.put(line_split[0], line_split[1]);
		}
		return (HashMap<String, String>) env;
	}
}
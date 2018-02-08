package comp557lw.demo;

public class ShaderInfo {
	int type = 0;
	String fileName = "";
	int shader = 0;
	
	public ShaderInfo() {}
	
	public ShaderInfo( int type, String fileName ) {
		this.type = type;
		this.fileName = fileName;
	}
}

package synthesijer.lib.upl;

public class UPLPortTest {
	
	private final UPLPort port = new UPLPort();
	
	public static int DATA_OFFSET = 4;
	public static int DATA_LENGTH = 3;
	
	
	private int ch;
	
	private void toLow(){
		if(ch >= 'A' && ch <= 'Z'){
			ch = ch - 'A' + 'a';
		}
	}
	
	public void run(){
		while(port.ready == false) ;
		port.op_start = true;
		port.send_length = port.recv_length;
				
		int len = port.data[DATA_LENGTH];
		for(int i = 0; i < (len >> 2); i++){
			int v  = port.data[i+DATA_OFFSET];
			int r = 0;
			ch = (v >> 0) & 0x000000FF;
			toLow();
			r += ch << 0;
			ch = (v >> 8) & 0x000000FF;
			toLow();
			r += ch << 8;
			ch = (v >> 16) & 0x000000FF;
			toLow();
			r += ch << 16;
			ch = (v >> 24) & 0x000000FF;
			toLow();
			r += ch << 24;
			port.data[i+DATA_OFFSET] = r;
		}
		
		port.op_start = false;
		port.op_done = true;
		port.op_done = false;
	}

}

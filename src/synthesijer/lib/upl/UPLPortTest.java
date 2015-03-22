package synthesijer.lib.upl;

public class UPLPortTest {
	
	private final UPLPort port = new UPLPort();
	
	public static int DATA_OFFSET = 4;
	public static int DATA_LENGTH = 3;
	
	private int toLow(int ch){
		int ret;
		if(ch >= 'A' && ch <= 'Z'){
			ret = ch - 'A' + 'a';
		}else{
			ret = ch;
		}
		return ret;
	}
	
	public void run(){
		while(port.ready == false) ;
		port.send_length = port.recv_length;
				
		int len = port.data[DATA_LENGTH];
		if((len & 0x3) == 0){
			len = len >> 2;
		}else{
			len = (len >> 2) + 1;
		}
		for(int i = 0; i < len; i++){
			int v  = port.data[i+DATA_OFFSET];
			int r = 0;
			r += (toLow((v >> 0) & 0x000000FF)) << 0;
			r += (toLow((v >> 8) & 0x000000FF)) << 8;
			r += (toLow((v >> 16) & 0x000000FF)) << 16;
			r += (toLow((v >> 24) & 0x000000FF)) << 24;
			port.data[i+DATA_OFFSET] = r;
		}
		port.done = true;
		port.done = false;
	}

}

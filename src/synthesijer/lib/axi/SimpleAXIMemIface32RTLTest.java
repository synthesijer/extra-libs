package synthesijer.lib.axi;

public class SimpleAXIMemIface32RTLTest{
	
	private final SimpleAXIMemIface32RTL obj = new SimpleAXIMemIface32RTL();
	
	public void write_data(int addr, int data){
		while(obj.busy == true){ ; } 
		obj.data[addr] = data;
	}
	
	public int read_data(int addr){
		while(obj.busy == true){ ; }
		int v = obj.data[addr];
		while(obj.busy == true){ ; }
		return obj.read_result;
	}
	
	public void test(){
		
		int offset = 0x3F000000;
		
		for(int j = 120; j < 150; j++){
			int y = (j << 10) + (j << 9) + (j << 8) + (j << 7);
			y = (y << 1) + y; // y = y * 3
			for(int i = 0; i < 30; i++){
				int pt = 120 + (i << 2) + y;
				write_data(pt + offset, 0xFFFFFFFF);
			}
		}
		
	}

}

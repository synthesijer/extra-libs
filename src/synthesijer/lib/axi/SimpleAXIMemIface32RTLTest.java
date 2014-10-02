package synthesijer.lib.axi;

public class SimpleAXIMemIface32RTLTest{
	
	private final SimpleAXIMemIface32RTL obj = new SimpleAXIMemIface32RTL();
	
	public void write_data(int addr, int data){
		while(obj.busy == true){ ; } 
		obj.data[addr] =  data;
	}
	
	public int read_data(int addr){
		while(obj.busy == true){ ; }
		return obj.data[addr];
	}
	
	public void test(){
		
		int offset = 0x3F000000;
		
		for(int i = 0; i < 10000; i++){
			write_data(i + offset, 0xFFFFFF);
		}
		
	}

}

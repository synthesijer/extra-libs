package synthesijer.lib.axi;


public class AXIMemIface32RTLTest{
	
	private final AXIMemIface32RTL obj = new AXIMemIface32RTL();
	
	public void flush(int addr, int burst){
		while(obj.busy == true){ ; }
		obj.axi_addr = addr;
		obj.burst_size = burst;
		obj.write_kick = true;
		obj.write_kick = false;
	}
	
	public void fetch(int addr, int burst){
		while(obj.busy == true){ ; }
		obj.axi_addr = addr;
		obj.burst_size = burst;
		obj.read_kick = true;
		obj.read_kick = false;
		while(obj.busy == true){ ; }
	}
	
	public int read(int offset){
		return obj.data[offset];
	}
	
	public void write(int offset, int data){
		obj.data[offset] = data;
	}

}

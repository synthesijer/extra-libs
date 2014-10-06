package synthesijer.utils;

public class CachedMemory {
	
	private final CacheMemoeryWrapper mem = new CacheMemoeryWrapper();
	
	public void write_data(int addr, int data){
		while(mem.busy == true) ;
		mem.data[addr] = data;
	}
	
	public int read_data(int addr){
		while(mem.busy == true) ;
		int v = mem.data[addr];
		return v;
	}

}

package synthesijer.utils;

public class ExternalMemory32UserSample {
	
	private final ExternalMemory32 mem = new ExternalMemory32();
	
	public void test(){
		for(int i = 0; i < 128; i++){
			mem.data[i+128] = mem.data[i];
		}
	}

}

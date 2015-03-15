package synthesijer.lib.axi;

import synthesijer.hdl.HDLSignalBinding;

public class AxiBinding extends HDLSignalBinding{
	
	private final boolean masterFlag;
	private final boolean addressSpaceFlag;
	private final boolean memoryMapFlag;
	private final long range;
	
	public AxiBinding(String name, boolean masterFlag, boolean addressSpaceFlag, boolean memoryMapFlag, long range){
		super(name);
		this.masterFlag = masterFlag;
		this.addressSpaceFlag = addressSpaceFlag;
		this.memoryMapFlag = memoryMapFlag;
		this.range = range;
	}

	public HDLSignalBinding export(String prefix){
		return new AxiBinding(prefix + "_" + name, masterFlag, addressSpaceFlag, memoryMapFlag, range);
	}
	
	public String getVendor(){
		return "xilinx.com";
	}

	public String getLibrary(){
		return "interface";
	}
	
	public String getBusTypeName(){
		return "aximm";
	}
	
	public String getBusAbstractionTypeName(){
		return "aximm_rtl";
	}
	
	public String getVersion(){
		 return "1.0";	
	}

	public boolean isMaster(){
		return masterFlag;
	}

	public boolean hasAddressSpace(){
		return addressSpaceFlag;
	}

	public boolean hasMemoryMap(){
		return memoryMapFlag;
	}

	public String getAddressBlockName(){
		return "";
	}

	public long getRange(){
		System.out.println("get range:" + range + " class:" + this);
		return range;
	}
	
}

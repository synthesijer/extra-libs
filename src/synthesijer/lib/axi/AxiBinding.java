package synthesijer.lib.axi;

import synthesijer.hdl.HDLSignalBinding;

public class AxiBinding extends HDLSignalBinding{
	
	private final boolean masterFlag;
	private final boolean addressSpaceFlag;
	private final boolean memoryMapFlag;
	
	public AxiBinding(String name, boolean masterFlag, boolean addressSpaceFlag, boolean memoryMapFlag){
		super(name);
		this.masterFlag = masterFlag;
		this.addressSpaceFlag = addressSpaceFlag;
		this.memoryMapFlag = memoryMapFlag;
	}

	public HDLSignalBinding export(String prefix){
		return new AxiBinding(prefix + "_" + name, masterFlag, addressSpaceFlag, memoryMapFlag);
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

}

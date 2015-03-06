package synthesijer.lib.axi;

import synthesijer.hdl.HDLSignalBinding;

public class AxiBinding extends HDLSignalBinding{
	
	public AxiBinding(String name){
		super(name);
	}

/*
  <spirit:busInterfaces>
    <spirit:busInterface>
      <spirit:name>M_AXI</spirit:name>
      <spirit:busType spirit:vendor="xilinx.com" spirit:library="interface" spirit:name="aximm" spirit:version="1.0"/>
      <spirit:abstractionType spirit:vendor="xilinx.com" spirit:library="interface" spirit:name="aximm_rtl" spirit:version="1.0"/>
      <spirit:master>
        <spirit:addressSpaceRef spirit:addressSpaceRef="M_AXI"/>
      </spirit:master>
      <spirit:portMaps>
        <spirit:portMap>
          <spirit:logicalPort>
            <spirit:name>AWADDR</spirit:name>
          </spirit:logicalPort>
          <spirit:physicalPort>
            <spirit:name>M_AXI_awaddr</spirit:name>
          </spirit:physicalPort>
        </spirit:portMap>
	      
*/
	
	public HDLSignalBinding export(String prefix){
		return new AxiBinding(prefix + "_" + name);
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
		return true;
	}

	public boolean hasAddressSpace(){
		return true;
	}

	public boolean hasMemoryMap(){
		return false;
	}

	public String getAddressBlockName(){
		return "";
	}

}

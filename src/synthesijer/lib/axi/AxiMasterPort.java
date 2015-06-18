package synthesijer.lib.axi;

import synthesijer.hdl.HDLModule;

public class AxiMasterPort {
	
	public final AxiMasterWritePort writer; 
	public final AxiMasterReadPort reader;
	
	public AxiMasterPort(HDLModule hm, String prefix, int width, long range){
		
		reader = new AxiMasterReadPort(hm, prefix + "_reader_", width);
		reader.setDefaultSetting();
		
		writer = new AxiMasterWritePort(hm, prefix + "_writer_", width);
		writer.setDefaultSetting();
		
		AxiBinding binding = new AxiBinding("axi", true, true, false, range);
		hm.addSignalBinding(binding);

		binding.set(reader.araddr, "ARADDR");
		binding.set(reader.arlen, "ARLEN");
		binding.set(reader.arsize, "ARSIZE");
		binding.set(reader.arburst, "ARBURST");
		binding.set(reader.arcache, "ARCACHE");
		binding.set(reader.arprot, "ARPROT");
		binding.set(reader.arvalid, "ARVALID");
		binding.set(reader.arready, "ARREADY");
		
		binding.set(reader.rdata, "RDATA");
		binding.set(reader.rresp, "RRESP");
		binding.set(reader.rlast, "RLAST");
		binding.set(reader.rvalid, "RVALID");
		binding.set(reader.rready, "RREADY");

		binding.set(writer.awaddr, "AWADDR");
		binding.set(writer.awlen, "AWLEN");
		binding.set(writer.awsize, "AWSIZE");
		binding.set(writer.awburst, "AWBURST");
		binding.set(writer.awcache, "AWCACHE");
		binding.set(writer.awprot, "AWPROT");
		binding.set(writer.awready, "AWREADY");
		binding.set(writer.awvalid, "AWVALID");
		
		binding.set(writer.wdata, "WDATA");
		binding.set(writer.wlast, "WLAST");
		binding.set(writer.wvalid, "WVALID");
		binding.set(writer.wready, "WREADY");
		binding.set(writer.wstrb, "WSTRB");
		
		binding.set(writer.bresp, "BRESP");
		binding.set(writer.bvalid, "BVALID");
		binding.set(writer.bready, "BREADY");
	}

}

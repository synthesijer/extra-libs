package synthesijer.lib.axi;

import synthesijer.hdl.HDLModule;

public class AxiLiteSlavePort {
	
	public final AxiLiteSlaveWritePort writer; 
	public final AxiLiteSlaveReadPort reader;
	
	public AxiLiteSlavePort(HDLModule hm, String prefix, int width, long range){
		
		reader = new AxiLiteSlaveReadPort(hm, prefix + "_reader_", width);
		reader.setDefaultSetting(width);
		
		writer = new AxiLiteSlaveWritePort(hm, prefix + "_writer_", width);
		writer.setDefaultSetting(width);
		
		AxiBinding binding = new AxiBinding("axi", false, false, true, range);
		hm.addSignalBinding(binding);

		binding.set(reader.araddr, "ARADDR");
		binding.set(reader.arvalid, "ARVALID");
		binding.set(reader.arready, "ARREADY");
		
		binding.set(reader.rdata, "RDATA");
		binding.set(reader.rresp, "RRESP");
		binding.set(reader.rvalid, "RVALID");
		binding.set(reader.rready, "RREADY");

		binding.set(writer.awaddr, "AWADDR");
		binding.set(writer.awready, "AWREADY");
		binding.set(writer.awvalid, "AWVALID");
		
		binding.set(writer.wdata, "WDATA");
		binding.set(writer.wvalid, "WVALID");
		binding.set(writer.wready, "WREADY");
		binding.set(writer.wstrb, "WSTRB");
		
		binding.set(writer.bresp, "BRESP");
		binding.set(writer.bvalid, "BVALID");
		binding.set(writer.bready, "BREADY");
	}

}

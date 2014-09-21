package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.utils.Utils;

public class AxiMasterReadPort {
	
	public final HDLPort araddr, arlen, arsize, arburst, arcache, arprot, arvalid, arready;
	public final HDLPort rdata, rresp, rlast, rvalid, rready;
	
	public AxiMasterReadPort(HDLModule m, String prefix, int axi_width){
		araddr  = Utils.genOutputPort(m, prefix + "ARADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		arlen   = Utils.genOutputPort(m, prefix + "ARLEN", 8, EnumSet.of(HDLPort.OPTION.EXPORT));
		arsize  = Utils.genOutputPort(m, prefix + "ARSIZE", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		arburst = Utils.genOutputPort(m, prefix + "ARBURST", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		arcache = Utils.genOutputPort(m, prefix + "ARCACHE", 4, EnumSet.of(HDLPort.OPTION.EXPORT));
		arprot  = Utils.genOutputPort(m, prefix + "ARPROT", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		arvalid = Utils.genOutputPort(m, prefix + "ARVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		arready = Utils.genInputPort(m, prefix + "ARREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		rdata   = Utils.genInputPort(m, prefix + "RDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		rresp   = Utils.genInputPort(m, prefix + "RRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		rlast   = Utils.genInputPort(m, prefix + "RLAST", EnumSet.of(HDLPort.OPTION.EXPORT));
		rvalid  = Utils.genInputPort(m, prefix + "RVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		rready  = Utils.genOutputPort(m, prefix + "RREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
}

package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.utils.Utils;

public class AxiMasterReadPort {
	
	public final HDLPort araddr, arlen, arsize, arburst, arcache, arprot, arvalid, arready;
	public final HDLPort rdata, rresp, rlast, rvalid, rready;
	
	public AxiMasterReadPort(HDLModule m, String prefix, int axi_width){
		araddr  = Utils.genOutputPort(m, prefix + "ARADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		arlen   = Utils.genOutputPort(m, prefix + "ARLEN", 8, EnumSet.of(HDLPort.OPTION.EXPORT));
		arvalid = Utils.genOutputPort(m, prefix + "ARVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		arready = Utils.genInputPort(m, prefix + "ARREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		arsize  = Utils.genOutputPort(m, prefix + "ARSIZE", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		arburst = Utils.genOutputPort(m, prefix + "ARBURST", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		arcache = Utils.genOutputPort(m, prefix + "ARCACHE", 4, EnumSet.of(HDLPort.OPTION.EXPORT));
		arprot  = Utils.genOutputPort(m, prefix + "ARPROT", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		
		rdata   = Utils.genInputPort(m, prefix + "RDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		rresp   = Utils.genInputPort(m, prefix + "RRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		rlast   = Utils.genInputPort(m, prefix + "RLAST", EnumSet.of(HDLPort.OPTION.EXPORT));
		rvalid  = Utils.genInputPort(m, prefix + "RVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		rready  = Utils.genOutputPort(m, prefix + "RREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
	public void setDefaultSetting(int width){
		switch(width){
		case   8: arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3))); break;
		case  16: arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b001), HDLPrimitiveType.genVectorType(3))); break;
		case  32: arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b010), HDLPrimitiveType.genVectorType(3))); break;
		case  64: arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b011), HDLPrimitiveType.genVectorType(3))); break;
		case 128: arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b100), HDLPrimitiveType.genVectorType(3))); break;
		case 256: arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b101), HDLPrimitiveType.genVectorType(3))); break;
		case 512: arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b110), HDLPrimitiveType.genVectorType(3))); break;
		default: arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3))); break;
		}

		// Burst type encoding: INCR
		arburst.getSignal().setAssign(null, new HDLValue(String.valueOf(0b01), HDLPrimitiveType.genVectorType(2)));
		// Normal Non-cache-able Buffer
		arcache.getSignal().setAssign(null, new HDLValue(String.valueOf(0b0011), HDLPrimitiveType.genVectorType(4)));
		// protocol
		arprot.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3)));
	}

}

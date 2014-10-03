package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.utils.Utils;

public class AxiMasterWritePort {
	
	public final HDLPort awaddr, awlen, awsize, awburst, awcache, awprot, awvalid, awready;
	public final HDLPort wdata, wstrb, wlast, wvalid, wready;
	public final HDLPort bresp, bvalid, bready;
	
	public AxiMasterWritePort(HDLModule m, String prefix, int axi_width){
		awaddr  = Utils.genOutputPort(m, prefix + "AWADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		awlen   = Utils.genOutputPort(m, prefix + "AWLEN", 8, EnumSet.of(HDLPort.OPTION.EXPORT));
		awvalid = Utils.genOutputPort(m, prefix + "AWVALID", EnumSet.of(HDLPort.OPTION.EXPORT));

		awsize  = Utils.genOutputPort(m, prefix + "AWSIZE", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		awburst = Utils.genOutputPort(m, prefix + "AWBURST", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		awcache = Utils.genOutputPort(m, prefix + "AWCACHE", 4, EnumSet.of(HDLPort.OPTION.EXPORT));
		awprot  = Utils.genOutputPort(m, prefix + "AWPROT", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		awready = Utils.genInputPort(m, prefix + "AWREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		wdata   = Utils.genOutputPort(m, prefix + "WDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		wlast   = Utils.genOutputPort(m, prefix + "WLAST", EnumSet.of(HDLPort.OPTION.EXPORT));
		wvalid  = Utils.genOutputPort(m, prefix + "WVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		wready  = Utils.genInputPort(m, prefix + "WREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		wstrb   = Utils.genOutputPort(m, prefix + "WSTRB", axi_width/8, EnumSet.of(HDLPort.OPTION.EXPORT));

		bresp = Utils.genInputPort(m, prefix + "BRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		bvalid = Utils.genInputPort(m, prefix + "BVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		bready = Utils.genOutputPort(m, prefix + "BREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
	}
	
	public void setDefaultSetting(int width){
		switch(width){
		case   8: awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3))); break;
		case  16: awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b001), HDLPrimitiveType.genVectorType(3))); break;
		case  32: awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b010), HDLPrimitiveType.genVectorType(3))); break;
		case  64: awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b011), HDLPrimitiveType.genVectorType(3))); break;
		case 128: awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b100), HDLPrimitiveType.genVectorType(3))); break;
		case 256: awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b101), HDLPrimitiveType.genVectorType(3))); break;
		case 512: awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b110), HDLPrimitiveType.genVectorType(3))); break;
		default:  awsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3))); break;
		}

		// Burst type encoding: INCR
		awburst.getSignal().setAssign(null, new HDLValue(String.valueOf(0b01), HDLPrimitiveType.genVectorType(2)));
		// Normal Non-cache-able Buffer
		awcache.getSignal().setAssign(null, new HDLValue(String.valueOf(0b0011), HDLPrimitiveType.genVectorType(4)));
		// protocol
		awprot.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3)));
		
		// strobe
		wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0b1), HDLPrimitiveType.genVectorType(width/8)));

	}

}

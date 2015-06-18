package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLValue;

public class AxiMasterWritePort {
	
	public final HDLPort awaddr, awlen, awsize, awburst, awcache, awprot, awvalid, awready;
	public final HDLPort wdata, wstrb, wlast, wvalid, wready;
	public final HDLPort bresp, bvalid, bready;
	
	public AxiMasterWritePort(HDLModule m, String prefix, int axi_width){
		awaddr  = HDLUtils.genOutputPort(m, prefix + "AWADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		awlen   = HDLUtils.genOutputPort(m, prefix + "AWLEN", 8, EnumSet.of(HDLPort.OPTION.EXPORT));
		awvalid = HDLUtils.genOutputPort(m, prefix + "AWVALID", EnumSet.of(HDLPort.OPTION.EXPORT));

		awsize  = HDLUtils.genOutputPort(m, prefix + "AWSIZE", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		awburst = HDLUtils.genOutputPort(m, prefix + "AWBURST", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		awcache = HDLUtils.genOutputPort(m, prefix + "AWCACHE", 4, EnumSet.of(HDLPort.OPTION.EXPORT));
		awprot  = HDLUtils.genOutputPort(m, prefix + "AWPROT", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		awready = HDLUtils.genInputPort(m, prefix + "AWREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		wdata   = HDLUtils.genOutputPort(m, prefix + "WDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		wlast   = HDLUtils.genOutputPort(m, prefix + "WLAST", EnumSet.of(HDLPort.OPTION.EXPORT));
		wvalid  = HDLUtils.genOutputPort(m, prefix + "WVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		wready  = HDLUtils.genInputPort(m, prefix + "WREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		wstrb   = HDLUtils.genOutputPort(m, prefix + "WSTRB", axi_width/8, EnumSet.of(HDLPort.OPTION.EXPORT));

		bresp = HDLUtils.genInputPort(m, prefix + "BRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		bvalid = HDLUtils.genInputPort(m, prefix + "BVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		bready = HDLUtils.genOutputPort(m, prefix + "BREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
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
		switch(width){
		case   8: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0b1), HDLPrimitiveType.genVectorType(width/8))); break;
		case  16: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0b11), HDLPrimitiveType.genVectorType(width/8))); break;
		case  32: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xF), HDLPrimitiveType.genVectorType(width/8))); break;
		case  64: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFF), HDLPrimitiveType.genVectorType(width/8))); break;
		case 128: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFFFF), HDLPrimitiveType.genVectorType(width/8))); break;
		case 256: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFFFFFFFFL), HDLPrimitiveType.genVectorType(width/8))); break;
		case 512: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFFFFFFFFFFFFFFFFL), HDLPrimitiveType.genVectorType(width/8))); break;
		default:  wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(-1), HDLPrimitiveType.genVectorType(width/8))); break;
		}

	}

}

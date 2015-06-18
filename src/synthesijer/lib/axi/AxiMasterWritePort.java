package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.utils.SimpleFifo;
import synthesijer.utils.SimpleFifo128;
import synthesijer.utils.SimpleFifo16;
import synthesijer.utils.SimpleFifo256;
import synthesijer.utils.SimpleFifo32;
import synthesijer.utils.SimpleFifo512;
import synthesijer.utils.SimpleFifo64;
import synthesijer.utils.SimpleFifo8;

public class AxiMasterWritePort {
	
	public final HDLPort awaddr, awlen, awsize, awburst, awcache, awprot, awvalid, awready;
	public final HDLPort wdata, wstrb, wlast, wvalid, wready;
	public final HDLPort bresp, bvalid, bready;
	
	private final HDLModule m;
	private final int axi_width;
	
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

		this.m = m;
		this.axi_width = axi_width;
	}
	
	public void setDefaultSetting(){
		switch(axi_width){
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
		switch(axi_width){
		case   8: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0b1), HDLPrimitiveType.genVectorType(axi_width/8))); break;
		case  16: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0b11), HDLPrimitiveType.genVectorType(axi_width/8))); break;
		case  32: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xF), HDLPrimitiveType.genVectorType(axi_width/8))); break;
		case  64: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFF), HDLPrimitiveType.genVectorType(axi_width/8))); break;
		case 128: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFFFF), HDLPrimitiveType.genVectorType(axi_width/8))); break;
		case 256: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFFFFFFFFL), HDLPrimitiveType.genVectorType(axi_width/8))); break;
		case 512: wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(0xFFFFFFFFFFFFFFFFL), HDLPrimitiveType.genVectorType(axi_width/8))); break;
		default:  wstrb.getSignal().setAssign(null, new HDLValue(String.valueOf(-1), HDLPrimitiveType.genVectorType(axi_width/8))); break;
		}

	}

	/**
	 * 
	 * @param seq
	 * @param hdl_axi_addr destination AXI address to write
	 * @param hdl_burst_size burst size to write
	 * @param seq
	 * @param local_addr local memory address to read for writing into AXI
	 * @param local_oe local memory output-enable to read for writing into AXI
	 * @param local_rdata local memory read-port to read for writing into AXI
	 */
	public void genWriteSeq(
			                  HDLSequencer seq,
			                  HDLExpr write_kick, HDLSignal write_busy,
			                  HDLExpr hdl_axi_addr, HDLExpr hdl_burst_size,
			                  HDLSignal local_addr, HDLSignal local_oe, HDLSignal local_rdata
			){
		
		HDLModule fifo;
		switch(axi_width){
		case   8: fifo = new SimpleFifo8();   break;
		case  16: fifo = new SimpleFifo16();  break;
		case  32: fifo = new SimpleFifo32();  break;
		case  64: fifo = new SimpleFifo64();  break;
		case 128: fifo = new SimpleFifo128(); break;
		case 256: fifo = new SimpleFifo256(); break;
		case 512: fifo = new SimpleFifo512(); break;
		default:  fifo = new SimpleFifo(); break;
		}

		HDLInstance fifo_inst = m.newModuleInstance(fifo);
		fifo_inst.getSignalForPort(fifo.getSysClkName()).setAssign(null, m.getSysClk().getSignal());
		fifo_inst.getSignalForPort(fifo.getSysResetName()).setAssign(null, m.getSysReset().getSignal());
		
		HDLSignal fifo_we = m.newTmpSignal(HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);
		HDLSignal fifo_re = m.newTmpSignal(HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.WIRE);
		HDLSignal fifo_din = m.newTmpSignal(HDLPrimitiveType.genVectorType(axi_width), HDLSignal.ResourceKind.REGISTER);
		HDLSignal fifo_dout = m.newTmpSignal(HDLPrimitiveType.genVectorType(axi_width), HDLSignal.ResourceKind.WIRE);
		HDLSignal fifo_empty = m.newTmpSignal(HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.WIRE);
		HDLSignal fifo_full = m.newTmpSignal(HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.WIRE);
		HDLSignal fifo_count = m.newTmpSignal(HDLPrimitiveType.genVectorType(32), HDLSignal.ResourceKind.WIRE);
		
		fifo_inst.getSignalForPort("we").setAssign(null, fifo_we);
		fifo_inst.getSignalForPort("din").setAssign(null, fifo_din);
		fifo_inst.getSignalForPort("re").setAssign(null, fifo_re);
		fifo_dout.setAssign(null, fifo_inst.getSignalForPort("dout"));
		fifo_empty.setAssign(null, fifo_inst.getSignalForPort("empty"));
		fifo_full.setAssign(null, fifo_inst.getSignalForPort("empty"));
		fifo_count.setAssign(null, fifo_inst.getSignalForPort("count"));
		
		fifo_we.setDefaultValue(HDLPreDefinedConstant.LOW);
//		fifo_re.setDefaultValue(HDLPreDefinedConstant.LOW);
		
		HDLSignal fifo_push_count = m.newTmpSignal(HDLPrimitiveType.genVectorType(32), HDLSignal.ResourceKind.REGISTER);
		HDLExpr fifo_push_count_dec = m.newExpr(HDLOp.IF, m.newExpr(HDLOp.EQ, fifo_push_count, 0), HDLPreDefinedConstant.VECTOR_ZERO, m.newExpr(HDLOp.SUB, fifo_push_count, 1));
		
		bready.getSignal().setAssign(seq.getIdleState(), HDLPreDefinedConstant.HIGH);
		HDLSignal count = m.newTmpSignal(HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		HDLExpr local_addr_inc = m.newExpr(HDLOp.ADD, local_addr, 1);
		HDLExpr count_dec = m.newExpr(HDLOp.SUB, count, 1);
		
		HDLSignal fifo_re_flag = m.newTmpSignal(HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);
		fifo_re_flag.setDefaultValue(HDLPreDefinedConstant.LOW);
		fifo_re.setAssign(null, m.newExpr(HDLOp.AND, m.newExpr(HDLOp.AND, fifo_re_flag, wvalid.getSignal()), wready.getSignal()));
		wdata.getSignal().setAssign(null, fifo_dout);

		// IDLE
		awaddr.getSignal().setAssign(seq.getIdleState(), hdl_axi_addr);
		awlen.getSignal().setAssign(seq.getIdleState(), m.newExpr(HDLOp.DROPHEAD, m.newExpr(HDLOp.SUB, hdl_burst_size, 1), HDLUtils.value(24, 32))); // bust_size - 1
		awvalid.getSignal().setAssign(seq.getIdleState(), write_kick); // kick axi_writer
		write_busy.setAssign(seq.getIdleState(), write_kick);
		local_addr.setAssign(seq.getIdleState(), HDLPreDefinedConstant.VECTOR_ZERO);
		local_oe.setAssign(seq.getIdleState(), write_kick); 
		count.setAssign(seq.getIdleState(), hdl_burst_size);
		fifo_push_count.setAssign(seq.getIdleState(), hdl_burst_size);
		wvalid.getSignal().setAssign(seq.getIdleState(), HDLPreDefinedConstant.LOW); // de-assert, just after wready is asserted.
		wlast.getSignal().setAssign(seq.getIdleState(), HDLPreDefinedConstant.LOW); // de-assert, just after wready is asserted.
		SequencerState s0 = seq.addSequencerState();
		seq.getIdleState().addStateTransit(write_kick, s0);
		
		// s0, wait for awready
		awvalid.getSignal().setAssign(s0, m.newExpr(HDLOp.NOT, awready.getSignal())); // de-assert, just after awready is asserted.
		local_addr.setAssign(s0, m.newExpr(HDLOp.IF, awready.getSignal(), local_addr_inc, local_addr)); // read_ptr++  (read_ptr => 1)
		SequencerState s1 = seq.addSequencerState();
		s0.addStateTransit(awready.getSignal(), s1);
		
		// s1, bram latency
		fifo_we.setAssign(s1, m.newExpr(HDLOp.IF, m.newExpr(HDLOp.GT, fifo_push_count, 0), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		fifo_din.setAssign(s1, local_rdata); // buf0 <= local[0]
		fifo_push_count.setAssign(s1, fifo_push_count_dec);
		local_addr.setAssign(s1, local_addr_inc); // read_ptr++ (read_ptr => 2)
		SequencerState s2 = seq.addSequencerState();
		s1.addStateTransit(s2);

		// s2, bram latency
		fifo_we.setAssign(s2, m.newExpr(HDLOp.IF, m.newExpr(HDLOp.GT, fifo_push_count, 0), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		fifo_din.setAssign(s2, local_rdata); // buf0 <= local[1]
		fifo_push_count.setAssign(s2, fifo_push_count_dec);
		local_addr.setAssign(s2, local_addr_inc); // read_ptr++ (read_ptr => 3)
		SequencerState s3 = seq.addSequencerState();
		//fifo_re_flag.setAssign(s2, HDLPreDefinedConstant.HIGH); // in next, read start
		s2.addStateTransit(s3);

		// s3, start to write data
		fifo_we.setAssign(s3, m.newExpr(HDLOp.IF, m.newExpr(HDLOp.GT, fifo_push_count, 0), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		fifo_din.setAssign(s3, local_rdata); // buf0 <= local[1]
		fifo_push_count.setAssign(s3, fifo_push_count_dec);
		local_addr.setAssign(s3, local_addr_inc); // read_ptr++
		
		wlast.getSignal().setAssign(s3,
				m.newExpr(HDLOp.IF,
						   m.newExpr(HDLOp.EQ, count, 1),
						   // count == 1
						   m.newExpr(HDLOp.IF,
								      m.newExpr(HDLOp.AND, wvalid.getSignal(), wready.getSignal()),
								      HDLPreDefinedConstant.LOW, // last data are accepted 
								      HDLPreDefinedConstant.HIGH), // accepted are not accepted yet
							// count != 1
							m.newExpr(HDLOp.IF,
									   m.newExpr(HDLOp.EQ, count, 2),
							// count == 2
							m.newExpr(HDLOp.IF,
									   m.newExpr(HDLOp.AND, wvalid.getSignal(), wready.getSignal()),
									   HDLPreDefinedConstant.HIGH, // in next, last word
									   HDLPreDefinedConstant.LOW), // in next, not last word
							// count != 2
							HDLPreDefinedConstant.LOW)));
		wvalid.getSignal().setAssign(s3,
				m.newExpr(HDLOp.IF,
						m.newExpr(HDLOp.GT, count, 1),
						HDLPreDefinedConstant.HIGH,
						m.newExpr(HDLOp.IF,
								m.newExpr(HDLOp.AND, m.newExpr(HDLOp.EQ, count, 1), m.newExpr(HDLOp.AND, wvalid.getSignal(), wready.getSignal())),
								HDLPreDefinedConstant.LOW,
								HDLPreDefinedConstant.HIGH
								))); // de-assert, just after wready is asserted.
//		axi.writer.wdata.getSignal().setAssign(s3, fifo_dout);
//		fifo_re_flag.setAssign(s3, axi.writer.wready.getSignal());
		fifo_re_flag.setAssign(s3, HDLPreDefinedConstant.HIGH);
		count.setAssign(s3, m.newExpr(HDLOp.IF, m.newExpr(HDLOp.AND, m.newExpr(HDLOp.AND, wvalid.getSignal(), wready.getSignal()), m.newExpr(HDLOp.GT, count, 0)), count_dec, count));
		
		SequencerState s4 = seq.addSequencerState();
		s3.addStateTransit(m.newExpr(HDLOp.AND, m.newExpr(HDLOp.EQ, count, 1), m.newExpr(HDLOp.AND, wvalid.getSignal(), wready.getSignal())), s4);
		
		// s4
		s4.addStateTransit(bvalid.getSignal(), seq.getIdleState());
		wlast.getSignal().setAssign(s4, HDLPreDefinedConstant.LOW);
		wvalid.getSignal().setAssign(s4, HDLPreDefinedConstant.LOW);
	}

	
}

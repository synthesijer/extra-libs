package synthesijer.lib.axi;

import java.util.EnumSet;

import synthesijer.hdl.HDLExpr;
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

public class AxiMasterReadPort {
	
	public final HDLPort araddr, arlen, arsize, arburst, arcache, arprot, arvalid, arready;
	public final HDLPort rdata, rresp, rlast, rvalid, rready;
	
	private final HDLModule m;
	private final int axi_width;
	
	public AxiMasterReadPort(HDLModule m, String prefix, int axi_width){
		araddr  = HDLUtils.genOutputPort(m, prefix + "ARADDR", 32, EnumSet.of(HDLPort.OPTION.EXPORT));
		arlen   = HDLUtils.genOutputPort(m, prefix + "ARLEN", 8, EnumSet.of(HDLPort.OPTION.EXPORT));
		arvalid = HDLUtils.genOutputPort(m, prefix + "ARVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		arready = HDLUtils.genInputPort(m, prefix + "ARREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		arsize  = HDLUtils.genOutputPort(m, prefix + "ARSIZE", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		arburst = HDLUtils.genOutputPort(m, prefix + "ARBURST", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		arcache = HDLUtils.genOutputPort(m, prefix + "ARCACHE", 4, EnumSet.of(HDLPort.OPTION.EXPORT));
		arprot  = HDLUtils.genOutputPort(m, prefix + "ARPROT", 3, EnumSet.of(HDLPort.OPTION.EXPORT));
		
		rdata   = HDLUtils.genInputPort(m, prefix + "RDATA", axi_width, EnumSet.of(HDLPort.OPTION.EXPORT));
		rresp   = HDLUtils.genInputPort(m, prefix + "RRESP", 2, EnumSet.of(HDLPort.OPTION.EXPORT));
		rlast   = HDLUtils.genInputPort(m, prefix + "RLAST", EnumSet.of(HDLPort.OPTION.EXPORT));
		rvalid  = HDLUtils.genInputPort(m, prefix + "RVALID", EnumSet.of(HDLPort.OPTION.EXPORT));
		rready  = HDLUtils.genOutputPort(m, prefix + "RREADY", EnumSet.of(HDLPort.OPTION.EXPORT));
		
		this.axi_width = axi_width;
		this.m = m;
	}
	
	public void setDefaultSetting(){
		switch(axi_width){
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

	/**
	 * 
	 * @param seq HDLSequencer
	 * @param read_kick_edge kick expression to start
	 * @param read_state_busy busy flag
	 * @param axi_addr address to read
	 * @param axi_burst_size burst size to read
	 * @param local_addr local memory address to write read data from AXI
	 * @param local_we local memory write-enable to write read data from AXI
	 * @param local_wdata local memory data to write read data from AXI
	 */
	public void genReadSeq(
			                HDLSequencer seq,
			                HDLExpr read_kick_edge, HDLSignal read_state_busy,
			                HDLExpr axi_addr, HDLExpr axi_burst_size,
			                HDLSignal local_addr, HDLSignal local_we, HDLSignal local_wdata){
		rready.getSignal().setAssign(seq.getIdleState(), HDLPreDefinedConstant.HIGH);
		HDLSignal count = m.newTmpSignal(HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		HDLSignal addr_next = m.newTmpSignal(HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
	
		// IDLE
		arvalid.getSignal().setAssign(seq.getIdleState(), read_kick_edge); // kick axi_reader
		araddr.getSignal().setAssign(seq.getIdleState(), axi_addr);
		arlen.getSignal().setAssign(seq.getIdleState(), m.newExpr(HDLOp.DROPHEAD, m.newExpr(HDLOp.SUB, axi_burst_size, 1), HDLUtils.value(24, 32))); // bust_size - 1
		SequencerState s0 = seq.addSequencerState();
		count.setAssign(seq.getIdleState(), axi_burst_size);
		local_addr.setAssign(seq.getIdleState(), HDLPreDefinedConstant.VECTOR_ZERO);
		addr_next.setAssign(seq.getIdleState(), HDLPreDefinedConstant.VECTOR_ZERO);
		seq.getIdleState().addStateTransit(read_kick_edge, s0); // idle -> s0, when oe = '1'
		read_state_busy.setAssign(seq.getIdleState(), read_kick_edge); // to start read sequence
		local_we.setDefaultValue(HDLPreDefinedConstant.LOW);
	
		// S0
		arvalid.getSignal().setAssign(s0, m.newExpr(HDLOp.NOT, arready.getSignal()));
		SequencerState s1 = seq.addSequencerState();
		s0.addStateTransit(arready.getSignal(), s1); // s0 -> s1, when arready = '1'
	
		HDLExpr last_word = m.newExpr(HDLOp.EQ, count, 1);
		HDLExpr count_dec = m.newExpr(HDLOp.SUB, count, 1);
		HDLExpr addr_next_inc = m.newExpr(HDLOp.ADD, addr_next, 1);
	
		// S1
		local_wdata.setAssign(s1, rdata.getSignal());
		local_addr.setAssign(s1, addr_next);
		local_we.setAssign(s1, rvalid.getSignal());
		count.setAssign(s1, m.newExpr(HDLOp.IF, rvalid.getSignal(), count_dec, count));
		addr_next.setAssign(s1, m.newExpr(HDLOp.IF, rvalid.getSignal(), addr_next_inc, addr_next));
		s1.addStateTransit(m.newExpr(HDLOp.AND, rvalid.getSignal(), last_word), seq.getIdleState());
	}

}

package synthesijer.lib.axi;

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
import synthesijer.utils.FifoPort;
import synthesijer.utils.Utils;

public class AXI_Reader extends HDLModule{
	
	private FifoPort fifo;
	private AxiMasterReadPort port;
	private HDLPort req, busy;
	
	private HDLPort addr, len;

	public AXI_Reader(){
		super("axi_reader", "clk", "reset");
		int width = 256;
		newParameter("BUF_WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(width));
		
		fifo = new FifoPort(this, "fifo_", width);
		port = new AxiMasterReadPort(this, "S_AXI_", width);
		
		req = Utils.genInputPort(this, "request");
		busy = Utils.genOutputPort(this, "busy");
		
		addr = Utils.genInputPort(this, "addr", 32);
		len = Utils.genInputPort(this, "len", 8);
		
		fifo.wclk.getSignal().setAssign(null, getSysClk().getSignal());
		
		setDefaultSetting(port);
		
		genStateMachine(newSequencer("main"));
		
	}
	
	private void genStateMachine(HDLSequencer s){
		HDLSequencer.SequencerState idle = s.getIdleState();
		// after reset
		port.rready.getSignal().setAssign(idle, HDLPreDefinedConstant.HIGH);
		
		// IDLE: wait for request
		HDLSequencer.SequencerState init = s.addSequencerState("init");
		HDLExpr req_assert = newExpr(HDLOp.EQ, req.getSignal(), HDLPreDefinedConstant.HIGH);
		idle.addStateTransit(req_assert, init);
		busy.getSignal().setAssign(idle, req_assert);
		fifo.we.getSignal().setAssign(idle, HDLPreDefinedConstant.LOW);
		
		// INIT: check whether FIFO is full or not 
		HDLSequencer.SequencerState read0 = s.addSequencerState("read0");
		HDLExpr fifo_ready = newExpr(HDLOp.EQ, fifo.full.getSignal(), HDLPreDefinedConstant.LOW);
		init.addStateTransit(fifo_ready, read0); // init -> read0
		port.araddr.getSignal().setAssign(init, addr.getSignal());
		port.arvalid.getSignal().setAssign(init, HDLPreDefinedConstant.HIGH);
		port.arlen.getSignal().setAssign(init, len.getSignal());
		HDLSignal read_length = newSignal("read_length", HDLPrimitiveType.genSignedType(8));
		read_length.setAssign(init, port.arlen.getSignal());
		
		// READ0
		HDLExpr arready_high = newExpr(HDLOp.EQ, port.arready.getSignal(), HDLPreDefinedConstant.HIGH);
		port.arvalid.getSignal().setAssign(read0, newExpr(HDLOp.IF, arready_high, HDLPreDefinedConstant.LOW, HDLPreDefinedConstant.HIGH));
		HDLSequencer.SequencerState read = s.addSequencerState("read");
		read0.addStateTransit(arready_high, read); // read0 -> read
		
		// READ
		HDLExpr rvalid_high = newExpr(HDLOp.EQ, port.rvalid.getSignal(), HDLPreDefinedConstant.HIGH);
		HDLExpr rlast_high = newExpr(HDLOp.EQ, port.rlast.getSignal(), HDLPreDefinedConstant.HIGH);
		fifo.dout.getSignal().setAssign(read, port.rdata.getSignal()); // fifo.dout <- port.rdata
		fifo.we.getSignal().setAssign(read,
				newExpr(HDLOp.IF, rvalid_high, HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));
		read.addStateTransit(newExpr(HDLOp.EQ, newExpr(HDLOp.AND, rvalid_high, rlast_high), HDLPreDefinedConstant.HIGH), idle);
	}

	private void setDefaultSetting(AxiMasterReadPort port){
		// Bytes in transfer: 8
		port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b011), HDLPrimitiveType.genVectorType(3)));
		// Burst type encoding: INCR
		port.arburst.getSignal().setAssign(null, new HDLValue(String.valueOf(0b01), HDLPrimitiveType.genVectorType(2)));
		// Normal Non-cache-able Buffer
		port.arcache.getSignal().setAssign(null, new HDLValue(String.valueOf(0b0011), HDLPrimitiveType.genVectorType(4)));
		// protocol
		port.arprot.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3)));
	}
	
	public static void main(String... args){
		AXI_Reader reader = new AXI_Reader();
		HDLUtils.generate(reader, HDLUtils.VHDL);
		HDLUtils.generate(reader, HDLUtils.Verilog);
	}

}

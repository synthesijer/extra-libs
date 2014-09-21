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
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.utils.FifoPort;
import synthesijer.utils.Utils;

public class AXI_Reader extends HDLModule{
	
	private FifoPort fifo;
	private AxiMasterReadPort port;
	private HDLPort req, busy;
	
	private HDLPort addr, len;

	private HDLExpr arready_high, rvalid_high;
	
	private final int width;

	public AXI_Reader(int width){
		super("axi_reader", "clk", "reset");
		this.width = width;
		newParameter("BUF_WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(width));
		
		fifo = new FifoPort(this, "fifo_", width);
		port = new AxiMasterReadPort(this, "S_AXI_", width);
		
		req = Utils.genInputPort(this, "request");
		busy = Utils.genOutputPort(this, "busy");
		
		addr = Utils.genInputPort(this, "addr", 32);
		len = Utils.genInputPort(this, "len", 8);
		
		fifo.wclk.getSignal().setAssign(null, getSysClk().getSignal());
		
		arready_high = newExpr(HDLOp.EQ, port.arready.getSignal(), HDLPreDefinedConstant.HIGH);
		rvalid_high = newExpr(HDLOp.EQ, port.rvalid.getSignal(), HDLPreDefinedConstant.HIGH);

		setDefaultSetting(port);
		
		genStateMachine(newSequencer("main"));
		
	}
	
	HDLSignal read_length;
	private SequencerState genInit(HDLSequencer s){
		SequencerState state = s.addSequencerState("init");
		// INIT: check whether FIFO is full or not 
		port.araddr.getSignal().setAssign(state, addr.getSignal());
		port.arvalid.getSignal().setAssign(state, HDLPreDefinedConstant.HIGH);
		//port.arlen.getSignal().setAssign(state, len.getSignal());
		port.arlen.getSignal().setAssign(state, newExpr(HDLOp.SUB, len.getSignal(), HDLPreDefinedConstant.INTEGER_ONE));
		read_length = newSignal("read_length", HDLPrimitiveType.genSignedType(8));
		read_length.setAssign(state, len.getSignal());
		return state;
	}
	
	private SequencerState genRead0(HDLSequencer s){
		SequencerState state = s.addSequencerState("read0");
		port.arvalid.getSignal().setAssign(state, newExpr(HDLOp.IF, arready_high, HDLPreDefinedConstant.LOW, HDLPreDefinedConstant.HIGH));
		fifo.dout.getSignal().setAssign(state, port.rdata.getSignal()); // fifo.dout <- port.rdata
		fifo.we.getSignal().setAssign(state, rvalid_high);
		read_length.setAssign(state,
				newExpr(HDLOp.IF, rvalid_high,
						newExpr(HDLOp.SUB, read_length, HDLPreDefinedConstant.INTEGER_ONE),
						read_length));
		return state;
	}
	
	private SequencerState genRead(HDLSequencer s){
		SequencerState state = s.addSequencerState("read");
		fifo.dout.getSignal().setAssign(state, port.rdata.getSignal()); // fifo.dout <- port.rdata
		fifo.we.getSignal().setAssign(state, rvalid_high);
		read_length.setAssign(state,
				newExpr(HDLOp.IF, rvalid_high,
						newExpr(HDLOp.SUB, read_length, HDLPreDefinedConstant.INTEGER_ONE),
						read_length));
		return state;
	}
	
	private void genStateMachine(HDLSequencer s){
		SequencerState idle = s.getIdleState();
		// after reset
		port.rready.getSignal().setAssign(idle, HDLPreDefinedConstant.HIGH);

		// IDLE
		HDLExpr req_assert = newExpr(HDLOp.EQ, req.getSignal(), HDLPreDefinedConstant.HIGH);
		busy.getSignal().setAssign(idle, req_assert);
		fifo.we.getSignal().setAssign(idle, HDLPreDefinedConstant.LOW);
		
		SequencerState init = genInit(s);
		SequencerState read0 = genRead0(s);
		SequencerState read = genRead(s);
		
		// idle -> init
		idle.addStateTransit(req_assert, init);

		// init -> read0
		HDLExpr fifo_ready = newExpr(HDLOp.EQ, fifo.full.getSignal(), HDLPreDefinedConstant.LOW);
		init.addStateTransit(fifo_ready, read0);
		
		// READ0
		read0.addStateTransit(arready_high, read); // read0 -> read
		
		// READ
		HDLExpr rlast_high = newExpr(HDLOp.EQ, port.rlast.getSignal(), HDLPreDefinedConstant.HIGH);
		read.addStateTransit(newExpr(HDLOp.EQ, newExpr(HDLOp.AND, rvalid_high, rlast_high), HDLPreDefinedConstant.HIGH), idle);
		read.addStateTransit(newExpr(HDLOp.EQ, newExpr(HDLOp.AND, rvalid_high, rlast_high), HDLPreDefinedConstant.HIGH), idle);
		read.addStateTransit(newExpr(HDLOp.EQ, newExpr(HDLOp.AND, rvalid_high, newExpr(HDLOp.EQ, read_length, HDLPreDefinedConstant.INTEGER_ONE)), HDLPreDefinedConstant.HIGH), idle);
	}

	// for 256-bit width
	private void setDefaultSetting(AxiMasterReadPort port){
		// Bytes in transfer: 32
		switch(width){
		case   8: port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3))); break;
		case  16: port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b001), HDLPrimitiveType.genVectorType(3))); break;
		case  32: port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b010), HDLPrimitiveType.genVectorType(3))); break;
		case  64: port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b011), HDLPrimitiveType.genVectorType(3))); break;
		case 128: port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b100), HDLPrimitiveType.genVectorType(3))); break;
		case 256: port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b101), HDLPrimitiveType.genVectorType(3))); break;
		case 512: port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b110), HDLPrimitiveType.genVectorType(3))); break;
		default: port.arsize.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3))); break;
		}

		// Burst type encoding: INCR
		port.arburst.getSignal().setAssign(null, new HDLValue(String.valueOf(0b01), HDLPrimitiveType.genVectorType(2)));
		// Normal Non-cache-able Buffer
		port.arcache.getSignal().setAssign(null, new HDLValue(String.valueOf(0b0011), HDLPrimitiveType.genVectorType(4)));
		// protocol
		port.arprot.getSignal().setAssign(null, new HDLValue(String.valueOf(0b000), HDLPrimitiveType.genVectorType(3)));
	}
	
	public static void main(String... args){
		AXI_Reader reader = new AXI_Reader(64);
		HDLUtils.genHDLSequencerDump(reader);
		HDLUtils.generate(reader, HDLUtils.VHDL);
		HDLUtils.generate(reader, HDLUtils.Verilog);
	}

}

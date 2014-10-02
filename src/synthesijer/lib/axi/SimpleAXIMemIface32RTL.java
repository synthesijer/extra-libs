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
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.utils.FifoReadPort;
import synthesijer.utils.FifoWritePort;
import synthesijer.utils.Utils;

public class SimpleAXIMemIface32RTL extends HDLModule{
	
	/////////////////////////////////////////////////////////////////
	// dummy variables, for Synthesijer /////////////////////////////
	public boolean busy;
	public int data[];
	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////
	// RTL implementation
	
	public final HDLPort addr, wdata, rdata, we, oe, hdl_busy;
	
	public final HDLPort axi_addr, axi_len;
	
	public final HDLPort axi_writer_req, axi_writer_busy;
	public final FifoWritePort writer_data;
	
	public final HDLPort axi_reader_req, axi_reader_busy;
	public final FifoReadPort reader_data;
	
	private final HDLSignal read_state_busy;
		
	public SimpleAXIMemIface32RTL(String... args){
		super("simple_axi_memiface_32", "clk", "reset");
		
		addr = Utils.genInputPort(this, "data_address", 32);
		wdata = Utils.genInputPort(this, "data_din", 32);
		rdata = Utils.genOutputPort(this, "data_dout", 32);
		we = Utils.genInputPort(this, "data_we");
		oe = Utils.genInputPort(this, "data_oe");
		hdl_busy = Utils.genOutputPort(this, "busy");
		
		axi_addr = Utils.genInputPort(this, "axi_addr", 32);
		axi_addr.getSignal().setAssign(null, addr.getSignal());
		axi_len = Utils.genInputPort(this, "axi_len", 32);
		axi_len.getSignal().setResetValue(Utils.value(1, 32)); // fixed length
		
		axi_writer_req = Utils.genOutputPort(this, "axi_writer_req");
		axi_writer_busy = Utils.genInputPort(this, "axi_writer_busy");
		axi_reader_req = Utils.genOutputPort(this, "axi_reader_req");
		axi_reader_busy = Utils.genInputPort(this, "axi_reader_busy");
		
		writer_data = new FifoWritePort(this, "axi_writer_", 32);
		reader_data = new FifoReadPort(this, "axi_reader_", 32);
		
		read_state_busy = newSignal("read_state_busy", HDLPrimitiveType.genBitType());
		
		hdl_busy.getSignal().setAssign(null, newExpr(HDLOp.OR, axi_writer_busy.getSignal(), read_state_busy));
		
		genWriteSeq();
		genReadSeq();
	}
	
	private void genWriteSeq(){
		HDLSequencer seq = newSequencer("write_seq");
		axi_writer_req.getSignal().setAssign(seq.getIdleState(), we.getSignal()); // kick axi_writer
		writer_data.dout.getSignal().setAssign(seq.getIdleState(), wdata.getSignal()); // push data into FIFO
		writer_data.we.getSignal().setAssign(seq.getIdleState(), we.getSignal()); // push data into FIFO
		SequencerState wfd = seq.addSequencerState("wait_for_done");
		seq.getIdleState().addStateTransit(we.getSignal(), wfd); // idle -> wfd, when we = '1'
		
		axi_writer_req.getSignal().setAssign(wfd, HDLPreDefinedConstant.LOW); // signal to kick axi_writer is de-asserted
		writer_data.we.getSignal().setAssign(wfd, HDLPreDefinedConstant.LOW); // signal for fifo-write is de-asserted
		HDLExpr axi_done = newExpr(HDLOp.NOT, newExpr(HDLOp.OR, axi_writer_req.getSignal(), axi_writer_busy.getSignal()));
		wfd.addStateTransit(axi_done, seq.getIdleState()); // wfd -> idle
	}
		
	private void genReadSeq(){
		HDLSequencer seq = newSequencer("read_seq");
		axi_reader_req.getSignal().setAssign(seq.getIdleState(), oe.getSignal()); // kick axi_reader
		SequencerState wfd = seq.addSequencerState("wait_for_done");
		seq.getIdleState().addStateTransit(oe.getSignal(), wfd); // idle -> wfd, when oe = '1'
		read_state_busy.setAssign(seq.getIdleState(), oe.getSignal()); // to start read sequence
		
		axi_reader_req.getSignal().setAssign(wfd, HDLPreDefinedConstant.LOW); // signal to kick axi_reader is de-asserted
		SequencerState fifo_wait = seq.addSequencerState("fifo_wait");
		HDLExpr axi_done = newExpr(HDLOp.NOT, newExpr(HDLOp.OR, axi_reader_req.getSignal(), axi_reader_busy.getSignal()));
		wfd.addStateTransit(axi_done, fifo_wait);
		reader_data.re.getSignal().setAssign(wfd, axi_done); // fifo re is asserted when axi_done is asserted, immediately
		
		SequencerState fifo_read = seq.addSequencerState("fifo_read");
		fifo_wait.addStateTransit(fifo_read); // just skip a fifo latency
		
		rdata.getSignal().setAssign(fifo_read, reader_data.din.getSignal()); // grab read data
		read_state_busy.setAssign(fifo_read, HDLPreDefinedConstant.LOW); // de-assert busy flag
		fifo_read.addStateTransit(seq.getIdleState()); // return to idle state
	}
	
	public static void main(String... args){
		SimpleAXIMemIface32RTL m = new SimpleAXIMemIface32RTL();
		HDLUtils.genHDLSequencerDump(m);
		HDLUtils.genResourceUsageTable(m);
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
	}

}

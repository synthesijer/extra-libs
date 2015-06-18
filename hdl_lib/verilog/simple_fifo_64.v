`default_nettype none

module simple_fifo_64 #(parameter WIDTH = 64, parameter DEPTH = 8)
   (
    input wire 		   clk,
    input wire 		   reset,
   
    input wire 		   we,
    input wire [WIDTH-1:0] din,
    input wire 		   re,
    //output reg [WIDTH-1:0] dout,
    output wire [WIDTH-1:0] dout,
    output reg 		   empty,
    output reg 		   full,
    output reg [WIDTH-1:0] count
    );
   
    simple_fifo#(WIDTH=64, DEPTH=8) U(
	.clk(clk),
	.reset(reset),
	.we(we),
	.din(din),
	.re(re),
	.dout(dout),
	.empty(empty),
	.full(full),
	.count(count)
    );
	
endmodule
`default_nettype wire

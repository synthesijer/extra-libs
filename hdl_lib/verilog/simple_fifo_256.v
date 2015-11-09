`default_nettype none

module simple_fifo_256 #(parameter WIDTH = 256, parameter DEPTH = 8)
   (
    input wire 		   clk,
    input wire 		   reset,
   
    input wire 		   we,
    input wire [WIDTH-1:0] din,
    input wire 		   re,
    //output reg [WIDTH-1:0] dout,
    output wire [WIDTH-1:0] dout,
    output wire 	   empty,
    output wire 	   full,
    output wire [WIDTH-1:0] count
    );
   
    simple_fifo#(.WIDTH(WIDTH), .DEPTH(DEPTH)) U(
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

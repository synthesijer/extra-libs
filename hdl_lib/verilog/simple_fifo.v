`default_nettype none

module simple_fifo #(parameter WIDTH = 32, parameter DEPTH = 8)
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
   
   localparam FIFO_SIZE = 2**DEPTH;
   
   reg signed [31:0] 	   head;
   reg signed [31:0] 	   tail;
   reg 			   looped;
  
   (* ram_styple="distributed" *) reg [WIDTH-1:0] mem[0:FIFO_SIZE-1];
   
   reg signed [31:0] 	   head_tmp;
   reg signed [31:0] 	   tail_tmp;
   
   reg signed [31:0] 	   count_tmp = 32'h0;
   reg 			   looped_tmp;
   reg 			   full_tmp;
   reg 			   empty_tmp;
    
   assign dout = mem[tail_tmp];
   always @(posedge clk) begin
      head_tmp   = head;
      tail_tmp   = tail;
      looped_tmp = looped;
      count_tmp  = 32'h0;

      if (reset == 1'b1) begin
	 
         head_tmp   = 32'h0;
         tail_tmp   = 32'h0;
         looped_tmp = 1'b0;
         full_tmp   = 1'b0;
         empty_tmp  = 1'b1;
	 
      end else begin
	
	 // read operation
         if (re == 1'b1) begin
            if ((looped_tmp == 1'b1) || (head_tmp != tail_tmp)) begin
//               dout <= mem[tail_tmp];
	       
               if (tail_tmp == FIFO_SIZE- 1) begin
		  tail_tmp = 32'h0;
		  looped_tmp = 1'b0;
               end else begin
		  tail_tmp = tail_tmp + 1;
               end
            end
         end // if (re == 1'b1)
        
	 // write operation
         if (we == 1'b1) begin
            if ((looped_tmp == 1'b0) || (head_tmp != tail_tmp)) begin
               mem[head_tmp] <= din;
               if (head_tmp == FIFO_SIZE - 1) begin
		  head_tmp   = 32'h0;
		  looped_tmp = 1'b1;
               end else begin
		  head_tmp = head_tmp + 1;
               end
            end
         end // if (we == 1'b1)
	 
	 // empty, flag operation
         if (head_tmp == tail_tmp) begin
            if (looped_tmp == 1'b1) begin
               full_tmp  = 1'b1;
               empty_tmp = 1'b0;
               count_tmp = FIFO_SIZE;
            end else begin
               full_tmp  = 1'b0;
               empty_tmp = 1'b1;
               count_tmp = 32'h0;
            end
         end else begin
            empty_tmp = 1'b0;
            full_tmp  = 1'b0;
          
            if (head_tmp < tail_tmp) begin
               count_tmp = FIFO_SIZE + head_tmp - tail_tmp;
            end else begin
               count_tmp = head_tmp - tail_tmp;
            end
         end // else: !if(head_tmp == tail_tmp)
	 
	 head   <= head_tmp;
	 tail   <= tail_tmp;
	 looped <= looped_tmp;
	 count  <= count_tmp;
	 full   <= full_tmp;
	 empty  <= empty_tmp;
	 
      end // else: !if(reset == 1'b1)
      
   end // always @ (posedge clk)
  
endmodule // simple_fifo

`default_nettype wire


module top
  (
   input wire 	    CLK,
   input wire 	    RESET,
   
   inout wire 	    SCL,
   inout wire 	    SDA,

   output wire [3:0] GPIO_LED
   );

   wire 	    scl_pad_i;
   wire 	    scl_pad_o;
   wire 	    scl_padoen_o;
   wire 	    sda_pad_i;
   wire 	    sda_pad_o;
   wire 	    sda_padoen_o;

   reg [31:0] 	    clk_counter = 32'b0;

   (* mark_debug = "true", keep = "true" *)  wire[31:0] debug;
   
   (* mark_debug = "true", keep = "true" *)  wire[31:0] U_PEAK_out;
   (* mark_debug = "true", keep = "true" *)  wire[31:0] D_PEAK_out;
   (* mark_debug = "true", keep = "true" *)  wire[31:0] L_PEAK_out;
   (* mark_debug = "true", keep = "true" *)  wire[31:0] R_PEAK_out;
   (* mark_debug = "true", keep = "true" *)  wire DECIDE_FLAG_out;
   (* mark_debug = "true", keep = "true" *)  wire DETECT_L2R_out;
   (* mark_debug = "true", keep = "true" *)  wire DETECT_R2L_out;
   (* mark_debug = "true", keep = "true" *)  wire DETECT_U2D_out;
   (* mark_debug = "true", keep = "true" *)  wire DETECT_D2U_out;

   OpenCoresI2CMasterIface_Test
     U(
       .clk(CLK),
       .reset(RESET),
       .iface_obj_arst_i_exp_exp(1'b1),
       .iface_obj_scl_pad_i_exp_exp(scl_pad_i),
       .iface_obj_scl_pad_o_exp_exp(scl_pad_o),
       .iface_obj_scl_padoen_o_exp_exp(scl_padoen_o),
       .iface_obj_sda_pad_i_exp_exp(sda_pad_i),
       .iface_obj_sda_pad_o_exp_exp(sda_pad_o),
       .iface_obj_sda_padoen_o_exp_exp(sda_padoen_o),
       .debug_in(32'b0),
       .debug_we(1'b0),
       .debug_out(debug),
       .U_PEAK_in(32'b0),
       .U_PEAK_we(1'b0),
       .U_PEAK_out(U_PEAK_out),
       .D_PEAK_in(32'b0),
       .D_PEAK_we(1'b0),
       .D_PEAK_out(D_PEAK_out),
       .L_PEAK_in(32'b0),
       .L_PEAK_we(1'b0),
       .L_PEAK_out(L_PEAK_out),
       .R_PEAK_in(32'b0),
       .R_PEAK_we(1'b0),
       .R_PEAK_out(R_PEAK_out),
       .DECIDE_FLAG_in(1'b0),
       .DECIDE_FLAG_we(1'b0),
       .DECIDE_FLAG_out(DECIDE_FLAG_out),
       .DETECT_L2R_in(1'b0),
       .DETECT_L2R_we(1'b0),
       .DETECT_L2R_out(DETECT_L2R_out),
       .DETECT_R2L_in(1'b0),
       .DETECT_R2L_we(1'b0),
       .DETECT_R2L_out(DETECT_R2L_out),
       .DETECT_U2D_in(1'b0),
       .DETECT_U2D_we(1'b0),
       .DETECT_U2D_out(DETECT_U2D_out),
       .DETECT_D2U_in(1'b0),
       .DETECT_D2U_we(1'b0),
       .DETECT_D2U_out(DETECT_D2U_out)
       );

   assign SCL = scl_padoen_o ? 1'bz : scl_pad_o;
   assign SDA = sda_padoen_o ? 1'bz : sda_pad_o;
   
   assign scl_pad_i = SCL;
   assign sda_pad_i = SDA;
   
   assign GPIO_LED = clk_counter[22:19];

   always @(posedge CLK) begin
      clk_counter <= clk_counter + 1;
   end
   
endmodule // top


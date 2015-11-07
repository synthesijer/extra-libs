/*
  Copyright (c) 2015, miya
  All rights reserved.

  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

module top
  (
   input        CLK,
   output       AUDIO_R,
   output       AUDIO_L,
   input        RESET_N
   );

  wire         clk_audio;

  // audio output port
  wire         audio_r;
  wire         audio_l;
  assign AUDIO_R = audio_r;
  assign AUDIO_L = audio_l;

  // generate reset
  reg    reset_reg;
  reg    reset;
  always @(posedge CLK)
    begin
      reset_reg <= ~RESET_N;
      reset <= reset_reg;
    end

  // generate audio reset
  reg    reset_audio_reg;
  reg    reset_audio;
  always @(posedge clk_audio)
    begin
      reset_audio_reg <= ~RESET_N;
      reset_audio <= reset_audio_reg;
    end

  // PLL module example
  pll_module pll_module_0
    (
     .in_clk (CLK),
     .reset (reset),
     .out_clk (clk_audio)
     );

  AudioOutputExample AudioOutputExample_0
    (
     .clk (CLK),
     .reset (reset),
     .audio_obj_ext_audio_clk_exp (clk_audio),
     .audio_obj_ext_audio_reset_exp (reset_audio),
     .audio_obj_ext_audio_r_exp (audio_r),
     .audio_obj_ext_audio_l_exp (audio_l)
     );

endmodule

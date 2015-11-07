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

`timescale 1ns / 1ps

module testbench;
  parameter STEP = 20; // 20 ns: 50MHz
  parameter STEP_AUDIO = 56; // 56 ns: 18MHz
  parameter TICKS = 20000;

  reg clk;
  reg clk_audio;
  reg reset;
  reg reset_audio;
  wire audio_r;
  wire audio_l;

  initial
    begin
      $dumpfile("wave.vcd");
      $dumpvars(5, testbench);
      $monitor("R: %d L: %d", audio_r, audio_l);
    end

  // generate clock signal
  initial
    begin
      clk = 1'b1;
      forever
        begin
          #(STEP / 2) clk = ~clk;
        end
    end

  // generate audio clock signal
  initial
    begin
      clk_audio = 1'b1;
      forever
        begin
          #(STEP_AUDIO / 2) clk_audio = ~clk_audio;
        end
    end

  // generate reset signal
  initial
    begin
      reset = 1'b0;
      repeat (2) @(posedge clk) reset <= 1'b1;
      @(posedge clk) reset <= 1'b0;
    end

  // generate audio reset signal
  initial
    begin
      reset_audio = 1'b0;
      repeat (2) @(posedge clk_audio) reset_audio <= 1'b1;
      @(posedge clk_audio) reset_audio <= 1'b0;
    end

  // stop simulation after TICKS
  initial
    begin
      repeat (TICKS) @(posedge clk);
      $finish;
    end

  AudioOutputExample AudioOutputExample_0
    (
     .clk (clk),
     .reset (reset),
     .audio_obj_ext_audio_clk_exp (clk_audio),
     .audio_obj_ext_audio_reset_exp (reset_audio),
     .audio_obj_ext_audio_r_exp (audio_r),
     .audio_obj_ext_audio_l_exp (audio_l)
     );

endmodule

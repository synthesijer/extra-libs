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

import synthesijer.lib.audio.*;

import synthesijer.rt.*;

public class AudioOutputExample
{
  private final AudioOutputIface audio = new AudioOutputIface();

  @auto
  public void main()
  {
    int sample = 0;
    int adder = -1201;
    // オーディオ出力インターフェース初期化
    // init(int clock_divider);
    // clock_divider = (デルタシグマ変換クロック / オーディオサンプリングレート) - 1
    // ex: (18MHz / 48000Hz) - 1 = 374
    audio.init(374);

    while (true)
    {
      // 音声データ書き込み
      // writeData(int right_channel, int left_channel);
      // sampling_data: 16bit signed
      // オーディオバッファがfullの時は書き込めるまでブロックされる
      audio.writeData(sample, sample);
      // 三角波を出力
      sample += adder;
      if ((sample > 32767) || (sample < -32768))
      {
        sample -= adder;
        adder = -adder;
      }
    }
  }
}

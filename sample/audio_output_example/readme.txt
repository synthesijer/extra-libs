音声出力インターフェース・ライブラリの使用サンプルです。

●動作環境
Linux上でのビルドを想定しています。
Synthesijerは
http://synthesijer.github.io/web
のセットアップスクリプト（setup_[日付].sh）でインストールしていることを前提にしています。

●音声出力回路について
このライブラリで音声を出力するためには以下の回路を製作する必要があります。
schematic/audio_output_schematic.png : ΔΣ D/A変調 音声出力回路
schematic/breadboard_audio.png : ブレッドボードでの実装例

●コンパイル方法
cd audio_output_example
source $HOME/synthesijer/synthesijer_env.sh
make

●ファイル説明
AudioOutputExample.java : サンプル本体

top_module/top.v : HDL側トップモジュールのサンプル

bemicro_max10 ディレクトリ : BeMicro Max 10向けプロジェクトファイル。使用方法は bemicro_max10/readme.txt を参照してください。

de0-cv ディレクトリ : Terasic DE0-CV向けプロジェクトファイル。使用方法は de0-cv/readme.txt を参照してください。

testbench ディレクトリ : Icarus Verilogコンパイラ用シミュレーション・ソースコード。使用方法は testbench/readme.txt を参照してください。

●使用ライブラリファイル
extra-libs/hdl_lib/verilog:
audio_output.v  cdc_fifo.v  dual_clk_ram.v  shift_register_vector.v

extra-libs/src/synthesijer/lib/audio:
AudioOutputIface.java  AudioOutputWrapper.java

●連絡先
このライブラリとサンプルはSynthesijerユーザーのコミュニティにより開発されたものです。バグや問題点などは以下のページでご連絡ください。
https://github.com/synthesijer/extra-libs/issues

メンテナ : miya
https://github.com/miya4649

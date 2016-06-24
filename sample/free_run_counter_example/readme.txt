FreeRunCounterライブラリの使用サンプルです。
タイマーで1秒おきにLEDを点滅させます。

●動作環境
Linux上でのビルドを想定しています。
Synthesijerは
http://synthesijer.github.io/web
のセットアップスクリプト（setup_[日付].sh）でインストールしていることを前提にしています。

●コンパイル方法
cd free_run_counter_example
source $HOME/synthesijer/synthesijer_env.sh
make

●ファイル説明
FreeRunCounterExample.java : サンプル本体

top_module/top.v : HDL側トップモジュールのサンプル

bemicro_max10 ディレクトリ : BeMicro Max 10向けプロジェクトファイル。使用方法は bemicro_max10/readme.txt を参照してください。

testbench ディレクトリ : Icarus Verilogコンパイラ用シミュレーション・ソースコード。使用方法は testbench/readme.txt を参照してください。

●使用ライブラリファイル
extra-libs/hdl_lib/verilog:
led_array.v
free_run_counter32.v
free_run_counter64.v

extra-libs/src/synthesijer/lib/led:
LEDArrayIface.java  LEDArrayWrapper.java

extra-libs/src/synthesijer/lib/timer:
FreeRunCounter32Wrapper.java
FreeRunCounter32Iface.java
FreeRunCounter64Wrapper.java
FreeRunCounter64Iface.java

●ライブラリ仕様
FreeRunCounter32Iface クラス

reset()
カウンタの値を0にリセットします。

get()
現在のカウンタの値を返します。カウンタは毎サイクル、自動的にインクリメントされます。

cycleWait(int clocks)
前回cycleWait()が呼ばれた時点から[clocks]サイクルが経過するまで待ちます。
処理時間に影響されず一定のタイミングで実行したい場合に便利です。
この時点からカウントするのではないことに注意してください。前回からの経過ではなくこの時点からclocksサイクル待たせたい場合は、reset(); の後にcycleWait(clocks); を記述してください。
0 <= clocks < 0x80000000

FreeRunCounter64Iface クラス
64bit版のカウンタです。値はlong型です。
0 <= clocks < 0x8000000000000000

●連絡先
このライブラリとサンプルはSynthesijerユーザーのコミュニティにより開発されたものです。バグや問題点などは以下のページでご連絡ください。
https://github.com/synthesijer/extra-libs/issues

メンテナ : miya
https://github.com/miya4649

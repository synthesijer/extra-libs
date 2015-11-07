LEDインターフェース・ライブラリの使用サンプルです。

●動作環境
Linux上でのビルドを想定しています。
Synthesijerは
http://synthesijer.github.io/web
のセットアップスクリプト（setup_[日付].sh）でインストールしていることを前提にしています。

●コンパイル方法
cd led_array_example
source $HOME/synthesijer/synthesijer_env.sh
make

●ファイル説明
LEDArrayExample.java : サンプル本体

top_module/top.v : HDL側トップモジュールのサンプル

bemicro_max10 ディレクトリ : BeMicro Max 10向けプロジェクトファイル。使用方法は bemicro_max10/readme.txt を参照してください。

testbench ディレクトリ : Icarus Verilogコンパイラ用シミュレーション・ソースコード。使用方法は testbench/readme.txt を参照してください。

●使用ライブラリファイル
extra-libs/hdl_lib/verilog:
led_array.v

extra-libs/src/synthesijer/lib/led:
LEDArrayIface.java  LEDArrayWrapper.java

●連絡先
このライブラリとサンプルはSynthesijerユーザーのコミュニティにより開発されたものです。バグや問題点などは以下のページでご連絡ください。
https://github.com/synthesijer/extra-libs/issues

メンテナ : miya
https://github.com/miya4649

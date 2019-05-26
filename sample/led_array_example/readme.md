# LEDインターフェース・ライブラリの使用サンプル

## 動作環境

Linux上でのビルドを想定しています。

## コンパイル方法

```
cd led_array_example

make
```

## ファイル説明

LEDArrayExample.java : サンプル本体

top_module/top.v : HDL側トップモジュールのサンプル

bemicro_max10 ディレクトリ : BeMicro Max 10向けプロジェクトファイル。使用方法は bemicro_max10/readme.txt を参照してください。

max10fb ディレクトリ : 「FPGA電子工作スーパーキット」付録のMAX10-FB基板向けプロジェクトファイル。使用方法は max10fb/readme.txt を参照してください。

testbench ディレクトリ : Icarus Verilogコンパイラ用シミュレーション・ソースコード。使用方法は testbench/readme.txt を参照してください。

## 使用ライブラリファイル

extra-libs/hdl_lib/verilog:

led_array.v


extra-libs/src/synthesijer/lib/led:

LEDArrayIface.java

LEDArrayWrapper.java

## 連絡先

このライブラリとサンプルはSynthesijerユーザーのコミュニティにより開発されたものです。バグや問題点などは以下のページでご連絡ください。

https://github.com/synthesijer/extra-libs/issues

メンテナ : miya

https://github.com/miya4649

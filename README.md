# VoiceConvertion
## 概要
音声変換app
Worldを用いてリアルタイムの音声変換処理を行っている。
内部で基本周波数、ケプストラム、非周期性指標を抽出し、それらをもとに波形データを再合成している。
本アプリでは基本周波数(俗に言うピッチ)を操作することにより変換を行っている。

## 動作環境
Android 10 API29

## 利用ライブラリ
* World - https://github.com/mmorise/World
    + Copyright (c) 2010 M. Morise

## ライセンス
修正BSDライセンス
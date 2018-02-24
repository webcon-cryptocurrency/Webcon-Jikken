# Webcon-Jikken

## 概要  
proof of workによるブロックチェーンのセキュリティの安全性を検証するため、擬似的なハッシュ計算を行いデータの保存の様子を確かめるプログラムを作りました。  

## 使い方
観測者(host)と採掘者(miner)のアプリケーションとして、以下のファイルをjarファイルにまとめした。  
[host]  
 Block.java,DataB.java,Hostserver.java  
[miner]  
 Clientserver.java,Miners.java,RMIClient.java  
hostファイルを起動し、RMIサーバーが完全に起動したのを確認してからminerを起動させてください。hostに予め設定しておいた人数のminerが集まると観測を開始します。

# Android TicTocTac

使用AndroidStdio制作的井字棋小游戏，是24年上半学期做的期末作业

## 项目简介

`TicTocTac` 是一个简答的井字棋小游戏，采用了极大值极小值算法模拟AI行动，所以按照常理说是赢不了的吧。。。
制作时的失误，大家有好的改进方法可以提出来。

## 游戏模式

简单制作了两个模式，单局和无尽模式


### 单局模式

就是普通的井字棋

### 无尽模式

每当玩家或敌人达成三连时，对应纵列会被消除、左上角对应的分数会+1，
当没有格子时且不能消除对饮的图像时，游戏会结束，
结束游戏时会比较双方的分数，分数较高者获胜；否则则为平局。

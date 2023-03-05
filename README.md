# screenshot

## 前言

基于Java图形框架实现的截图工具，通过键盘和鼠标的监听完成截图动作，精准定位截图区域，并提取截图区域到剪贴板，代码简单。

在阅读外语文章或游玩外语游戏的场景，可自定义接入OCR提取文字，并接入翻译接口解决实时翻译的需求。

## 使用

- Ctrl + Q：框选截图，按住鼠标拖动框选区域，双击截屏至clipboard
- Print Screen：全屏截图，将Full Screen保存至clipboard
- Esc：退出

## 问答

- 出现中文乱码问题？

  启动参数vm options新增-Dfile.encoding=GBK

## 效果演示

![sample1](https://github.com/HyiKi/screenshot/blob/main/src/main/resources/sample1.png)

![sample2](https://github.com/HyiKi/screenshot/blob/main/src/main/resources/sample2.png)
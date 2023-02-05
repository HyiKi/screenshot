package com.hyiki.screenshot.utils;

import cn.hutool.core.swing.clipboard.ImageSelection;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;

/**
 * 系统工具 核心方法
 *
 * @author 王浩轩
 * @describe
 * @since 2022/10/27
 */
@Slf4j
public class SystemUtils {

    public static void captureToClipboard(Rectangle screenRect) throws Exception {
        BufferedImage capture = new Robot().createScreenCapture(screenRect);
        Clipboard clipboard = Toolkit.getDefaultToolkit()
                .getSystemClipboard();
        // img
        ImageSelection imageSelection = new ImageSelection(capture);
        clipboard.setContents(imageSelection, null);
    }

}

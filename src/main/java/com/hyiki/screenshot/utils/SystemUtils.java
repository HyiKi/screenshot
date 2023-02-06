package com.hyiki.screenshot.utils;

import cn.hutool.core.swing.clipboard.ImageSelection;
import com.hyiki.screenshot.enums.SystemEnum;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.util.Locale;

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

    public static SystemEnum getOS() {
        String osName = System.getProperty("os.name");
        if (osName == null) {
            throw new RuntimeException("os.name not found");
        }
        SystemEnum system = null;
        osName = osName.toLowerCase(Locale.ENGLISH);
        // match
        if (osName.contains("windows")) {
            system = SystemEnum.WINDOWS;
        } else if (osName.contains("linux") ||
                osName.contains("mpe/ix") ||
                osName.contains("freebsd") ||
                osName.contains("openbsd") ||
                osName.contains("irix") ||
                osName.contains("digital unix") ||
                osName.contains("unix") ||
                osName.contains("mac os x")) {
            system = SystemEnum.UNIX;
        } else if (osName.contains("sun os") ||
                osName.contains("sunos") ||
                osName.contains("solaris")) {
            system = SystemEnum.POSIX_UNIX;
        } else if (osName.contains("hp-ux") ||
                osName.contains("aix")) {
            system = SystemEnum.POSIX_UNIX;
        } else {
            system = SystemEnum.OTHER;
        }
        log.info("[os.name] {} [system] {}", osName, system);
        return system;
    }

}

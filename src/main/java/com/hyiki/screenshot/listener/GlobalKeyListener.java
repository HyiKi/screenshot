package com.hyiki.screenshot.listener;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.hyiki.screenshot.enums.SystemEnum;
import com.hyiki.screenshot.frame.CaptureFrame;
import com.hyiki.screenshot.frame.MouseComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author 王浩轩
 * @describe 全局按键监听器
 * @since 2023/2/1
 */
@Slf4j
public class GlobalKeyListener implements NativeKeyListener {

    private final CaptureFrame captureFrame;

    private final MouseComponent mouseComponent;

    private final GlobalKeyFunction globalKeyFunction = new GlobalKeyFunction();

    private final SystemEnum os;

    public GlobalKeyListener(CaptureFrame captureFrame, MouseComponent mouseComponent) {
        this.captureFrame = captureFrame;
        this.mouseComponent = mouseComponent;
        // init os
        os = getOS();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        log.info("Key Typed: {}", NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        log.info("Key Pressed: {}", keyText);
        globalKeyFunction.registerKey(keyText);
        globalKeyFunction.doThing();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        log.info("Key Released: {}", keyText);
        globalKeyFunction.unregisterKey(keyText);
    }

    /**
     * 全局按键作用器
     */
    private class GlobalKeyFunction {

        private final Set<String> keySet = new HashSet<>();

        /**
         * 注册
         */
        public void registerKey(String key) {
            keySet.add(key);
        }

        /**
         * 解除
         */
        public void unregisterKey(String key) {
            keySet.remove(key);
        }

        /**
         * 做事
         */
        public void doThing() {
            monitorScreenshot();
        }

        /**
         * 监控截图
         */
        public void monitorScreenshot() {
            // startScreenshot 开启截图
            switch (os) {
                case WINDOWS:
                    if (keySet.contains("Ctrl") && keySet.contains("Q")) {
                        startScreenshot();
                    }
                    break;
                case UNIX:
                    if (keySet.contains("⌃") && keySet.contains("Q")) {
                        startScreenshot();
                    }
                    break;
            }
            // finishAndClearScreenshot 关闭截图
            switch (os) {
                case WINDOWS:
                    if (keySet.contains("Esc")) {
                        finishAndClearScreenshot();
                    }
                    break;
                case UNIX:
                    if (keySet.contains("⎋")) {
                        finishAndClearScreenshot();
                    }
                    break;
            }
        }

        /**
         * CTRL + Q 开启截图
         */
        private void startScreenshot() {
            captureFrame.setVisible(true);
        }

        /**
         * ESC 关闭截图
         */
        private void finishAndClearScreenshot() {
            captureFrame.setVisible(false);
            mouseComponent.remove();
        }

    }

    public void register() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(this);
    }

    public SystemEnum getOS() {
        String osName = System.getProperty("os.name");
        log.info("osName is {}", osName);
        if (osName == null) {
            throw new RuntimeException("os.name not found");
        }
        osName = osName.toLowerCase(Locale.ENGLISH);
        // match
        if (osName.contains("windows")) {
            return SystemEnum.WINDOWS;
        } else if (osName.contains("linux") ||
                osName.contains("mpe/ix") ||
                osName.contains("freebsd") ||
                osName.contains("openbsd") ||
                osName.contains("irix") ||
                osName.contains("digital unix") ||
                osName.contains("unix") ||
                osName.contains("mac os x")) {
            return SystemEnum.UNIX;
        } else if (osName.contains("sun os") ||
                osName.contains("sunos") ||
                osName.contains("solaris")) {
            return SystemEnum.POSIX_UNIX;
        } else if (osName.contains("hp-ux") ||
                osName.contains("aix")) {
            return SystemEnum.POSIX_UNIX;
        } else {
            return SystemEnum.OTHER;
        }
    }

}

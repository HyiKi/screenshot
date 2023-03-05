package com.hyiki.screenshot.listener;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.hyiki.screenshot.enums.SystemEnum;
import com.hyiki.screenshot.frame.CaptureFrame;
import com.hyiki.screenshot.frame.MouseComponent;
import com.hyiki.screenshot.utils.SystemUtils;
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
        os = SystemUtils.getOS();
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
                    if (keySet.contains("Print Screen")) {
                        saveFullScreenToClipboard();
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
         * Print Screen 保存整个屏幕到剪贴板
         */
        private void saveFullScreenToClipboard() {
            log.info("saveFullScreenToClipboard");
            captureFrame.setVisible(false);
            try {
                SystemUtils.captureToClipboard(SystemUtils.getFullScreenRectangle());
            } catch (Exception e) {
                log.error("saveFullScreenToClipboard error {}", e.getMessage(), e);
            }
        }

        /**
         * CTRL + Q 开启截图
         */
        private void startScreenshot() {
            log.info("startScreenshot");
            captureFrame.setVisible(true);
        }

        /**
         * ESC 关闭截图
         */
        private void finishAndClearScreenshot() {
            log.info("finishAndClearScreenshot");
            mouseComponent.remove();
            captureFrame.setVisible(false);
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

}

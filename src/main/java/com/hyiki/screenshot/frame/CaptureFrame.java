package com.hyiki.screenshot.frame;

import com.hyiki.screenshot.listener.GlobalKeyListener;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.Base64;

/**
 * 截图主框架
 *
 * @author wanghaoxuan
 */
@Slf4j
public class CaptureFrame extends JFrame {

    /**
     * 系统托盘
     */
    private SystemTray tray;

    /**
     * 托盘icon
     */
    private TrayIcon trayIcon;

    /**
     * 鼠标监听组件
     */
    private MouseComponent mouseComponent;

    /**
     * 初始化截图框架
     */
    public CaptureFrame() {
        // init system tray
        initSystemTray();
        // init frame
        initFrame();
        // init jnativehook listener
        initJnativehookListener();
    }

    /**
     * 初始化系统托盘
     */
    private void initSystemTray() {
        // 系统托盘 (右下角任务栏) 获得本操作系统托盘的实例
        tray = SystemTray.getSystemTray();
        // 托盘图标
        byte[] trayIconBytes = Base64.getDecoder().decode(getIcon().getBytes());
        // 显示在托盘中的图标
        ImageIcon icon = new ImageIcon(trayIconBytes);
        // 这句很重要，没有会导致图片显示不出来
        trayIcon.setImageAutoSize(true);
        // 构造一个右键弹出式菜单
        PopupMenu pop = new PopupMenu();
        MenuItem exit = new MenuItem("退出");
        pop.add(exit);
        trayIcon = new TrayIcon(icon.getImage(), "screenshot", pop);
        // 注册退出监听器
        exit.addActionListener(e -> {
            // e means actionEvent
            tray.remove(trayIcon);
            System.exit(0);
        });
    }

    /**
     * 初始化按键监听器
     */
    private void initJnativehookListener() {
        GlobalKeyListener globalKeyListener = new GlobalKeyListener(this, mouseComponent);
        // 注册监听器
        globalKeyListener.register();
    }

    /**
     * 初始化框架
     */
    private void initFrame() {
        // init JFrame here
        // actionListen esc exit (ECS for exit)
//        ActionListener actionListener = e -> frame.dispose();
//        frame.getRootPane().registerKeyboardAction(actionListener, "command",
//                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
//                JComponent.WHEN_IN_FOCUSED_WINDOW);
        // kit
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dimension = kit.getScreenSize();
        // tray
        try {
            tray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置不可见
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setBounds(0, 0, dimension.width, dimension.height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setBackground(getBackgroundColor());
        // 窗口置顶
        this.setAlwaysOnTop(true);
        // 应用图标
        byte[] trayIconBytes = Base64.getDecoder().decode(getIcon().getBytes());
        // 显示在应用中的图标
        try {
            this.setIconImage(ImageIO.read(new ByteArrayInputStream(trayIconBytes)));
        } catch (Exception e) {
            log.error("加载应用图标失败...... load icon image failed.", e);
        }
        // 默认未激活
        this.setVisible(false);
        mouseComponent = new MouseComponent(this);
        add(mouseComponent);
        pack();
    }

    /**
     * 背景颜色
     */
    public static Color getBackgroundColor() {
        return new Color(0, 0, 0, 1);
    }

    /**
     * icon base64
     */
    public static String getIcon() {
        return "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";
    }

}

package com.hyiki.screenshot.starter;

import com.hyiki.screenshot.frame.CaptureFrame;

import javax.swing.*;
import java.awt.*;

public class CaptureStarter {

    public void start() {
        EventQueue.invokeLater(() -> {
            JFrame captureFrame = new CaptureFrame();
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame captureFrame = new CaptureFrame();
        });
    }

}

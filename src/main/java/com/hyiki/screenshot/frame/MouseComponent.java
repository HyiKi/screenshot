package com.hyiki.screenshot.frame;

import com.hyiki.screenshot.convertor.RectangleConvertor;
import com.hyiki.screenshot.dtos.ContentDTO;
import com.hyiki.screenshot.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 鼠标控件
 *
 * @author wanghaoxuan
 */
@Slf4j
public class MouseComponent extends JComponent {

    /**
     * the square containing the mouse cursor
     */
    private Rectangle2D squares;

    /**
     * content square
     */
    private Rectangle2D contentSquare;

    /**
     * press point on the axis
     */
    private Point pressPoint;

    /**
     * release point on the axis
     */
    private Point dragPoint;

    /**
     * release point on the axis
     */
    private Point releasePoint;

    /**
     * ocr content
     */
    private List<ContentDTO> contents;

    private final CaptureFrame captureFrame;

    private static final ThreadLocal<Runnable> RUN_AFTER = new ThreadLocal<>();

//    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final ExecutorService executorService = new ThreadPoolExecutor(1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            r -> new Thread(r, "capture-thread-" + r.hashCode()));

    /**
     * 构造方法
     */
    public MouseComponent(CaptureFrame captureFrame) {
        squares = null;
        pressPoint = null;
        dragPoint = null;
        releasePoint = null;
        this.captureFrame = captureFrame;
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
    }

    /**
     * 初始化画布大小
     */
    @Override
    public Dimension getPreferredSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * 执行绘制
     */
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        Rectangle screenRect = SystemUtils.getFullScreenRectangle();
        // 矩阵填充
        if (squares != null) {
//            g.setColor(getBorderColor());
//            g.draw(squares);
            g.setColor(getFillColor());
            // rectangle 1
            double leftTopX = screenRect.getX();
            double leftTopY = screenRect.getY();
            // top
            Rectangle2D rectangleTop = buildRectangle(leftTopX,
                    screenRect.getWidth(),
                    leftTopY,
                    squares.getY());
            g.fill(rectangleTop);
            // left
            Rectangle2D rectangleLeft = buildRectangle(leftTopX,
                    squares.getX(),
                    squares.getY(),
                    squares.getY() + squares.getHeight());
            g.fill(rectangleLeft);
            // right
            Rectangle2D rectangleRight = buildRectangle(squares.getX() + squares.getWidth(),
                    screenRect.getWidth(),
                    squares.getY(),
                    squares.getY() + squares.getHeight());
            g.fill(rectangleRight);
            // bottom
            Rectangle2D rectangleBottom = buildRectangle(leftTopX,
                    screenRect.getWidth(),
                    squares.getY() + squares.getHeight(),
                    screenRect.getHeight());
            g.fill(rectangleBottom);
        }
        // 全屏填充
        else {
            g.setColor(getFillColor());
            g.fill(screenRect);
        }
        // 文字绘制
        if (contents != null) {
            for (ContentDTO content : contents) {
                processContent(g, content);
            }
        }
        Runnable task = RUN_AFTER.get();
        if (task != null) {
            // 润
            executorService.submit(task);
            RUN_AFTER.remove();
        }
        log.info("paintComponent");
    }

    /**
     * 处理内容
     */
    private void processContent(Graphics2D g, ContentDTO contentDTO) {
    }

    /**
     * 两点绘制矩形
     */
    public void draw(Point p1, Point p2) {
        if (Objects.nonNull(p1) && Objects.nonNull(p2)) {
            double x1 = p1.getX();
            double x2 = p2.getX();
            double y1 = p1.getY();
            double y2 = p2.getY();
            squares = buildRectangle(x1, x2, y1, y2);
            repaint();
        }
    }

    /**
     * 两点坐标画矩阵
     */
    private Rectangle2D buildRectangle(double x1, double x2, double y1, double y2) {
        double x = Math.min(x1, x2);
        double y = Math.min(y1, y2);
        double w = Math.max(x1, x2) - x;
        double h = Math.max(y1, y2) - y;
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * 点是否位于矩形内
     */
    public boolean find(Point2D p) {
        return squares != null && squares.contains(p);
    }

    /**
     * 清空
     */
    public void remove() {
        pressPoint = null;
        dragPoint = null;
        releasePoint = null;
        squares = null;
        contents = null;
        repaint();
    }

    /**
     * border color
     */
    public Color getBorderColor() {
        return new Color(34, 168, 53);
    }

    /**
     * fill color
     */
    public Color getFillColor() {
        return new Color(0, 0, 0, 128);
    }

    /**
     * font color
     */
    public Color getFontColor() {
        return new Color(0, 0, 0, 255);
    }

    private class MouseHandler extends MouseAdapter {

        /**
         * 鼠标按下时调用
         */
        @Override
        public void mousePressed(MouseEvent event) {
            Point current = event.getPoint();
            // 点出画面外，重新绘制
            if (!find(current)) {
                remove();
                pressPoint = new Point(current);
            }
            log.info("[pressed]" + current.getX() + "," + current.getY());
        }

        /**
         * 鼠标点击时调用
         */
        @Override
        public void mouseClicked(MouseEvent event) {
            Point current = event.getPoint();
            // 点击两次则移除矩形
            int doubleClickCount = 2;
            if (find(current) && event.getClickCount() >= doubleClickCount) {
                // capture rectangle
                Rectangle rectangle = RectangleConvertor.convert2Rectangle(squares);
                remove();
                // 这里触发截图动作
                registerRunAfter(() -> this.saveCaptureAndExit(rectangle));
            }
            log.info("[clicked]" + current.getX() + "," + current.getY());
        }

        private void registerRunAfter(Runnable task) {
            RUN_AFTER.set(task);
        }

        /**
         * 保存截图到粘贴板且退出
         */
        private void saveCaptureAndExit(Rectangle rectangle) {
            // save capture to the clipboard
            try {
                captureFrame.setVisible(false);
                SystemUtils.captureToClipboard(rectangle);
            } catch (Exception e) {
                log.error("capture error", e);
            }
            // 在后台运行，不退出
//            System.exit(0);
        }

        /**
         * 鼠标松开时调用
         */
        @Override
        public void mouseReleased(MouseEvent event) {
            super.mouseReleased(event);
            Point current = event.getPoint();
            releasePoint = new Point(current);
            if (!Objects.equals(squares, contentSquare)) {
                contents = callForContents();
                repaint();
            }
            if (squares == null && pressPoint != null) {
                draw(pressPoint, releasePoint);
            }
            log.info("[released]" + current.getX() + "," + current.getY());
        }

        /**
         * 外部调用获取框内文字
         */
        private List<ContentDTO> callForContents() {
            return null;
        }
    }

    private class MouseMotionHandler implements MouseMotionListener {

        /**
         * 鼠标移动时调用
         */
        @Override
        public void mouseMoved(MouseEvent event) {
            // set cursor
            if (find(event.getPoint())) {
                setCursor(Cursor.getDefaultCursor());
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        }

        /**
         * 鼠标按住拖动时调用
         */
        @Override
        public void mouseDragged(MouseEvent event) {
            Point current = event.getPoint();
            if (releasePoint == null) {
                // dragMotion
                dragPoint = new Point(current);
                draw(pressPoint, dragPoint);
            }
            log.info("[drag]" + current.getX() + "," + current.getY());
        }

    }
}

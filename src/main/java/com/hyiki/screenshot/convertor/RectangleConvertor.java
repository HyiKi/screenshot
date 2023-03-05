package com.hyiki.screenshot.convertor;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class RectangleConvertor {

    public static Rectangle convert2Rectangle(Rectangle2D squares) {
        int x = (int) squares.getX();
        int y = (int) squares.getY();
        int w = (int) squares.getWidth();
        int h = (int) squares.getHeight();
        return new Rectangle(x, y, w, h);
    }

}

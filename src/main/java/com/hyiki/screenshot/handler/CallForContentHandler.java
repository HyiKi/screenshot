package com.hyiki.screenshot.handler;

import com.hyiki.screenshot.dtos.ContentDTO;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * 外部调用获取框内文字
 */
public interface CallForContentHandler {

    List<ContentDTO> callForContents(Rectangle2D contentSquare);

}

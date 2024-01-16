package com.hyiki.screenshot.handler;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.hyiki.screenshot.convertor.RectangleConvertor;
import com.hyiki.screenshot.dtos.ContentDTO;
import com.hyiki.screenshot.utils.HttpUtils;
import com.hyiki.screenshot.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultContentHandler implements CallForContentHandler {

    @Override
    public List<ContentDTO> callForContents(Rectangle2D contentSquare) {
        File tempFile = null;
        Rectangle rectangle = RectangleConvertor.convert2Rectangle(contentSquare);
        try {
            tempFile = File.createTempFile(RandomUtil.randomString(10), ".jpg");
            String absolutePath = tempFile.getAbsolutePath();
            SystemUtils.captureRectangle(rectangle, absolutePath);
            return postLocalOcrService(absolutePath);
        } catch (Exception e) {
            log.error("callOcr error", e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                String absolutePath = tempFile.getAbsolutePath();
                boolean delete = tempFile.delete();
                log.info("tempFile {} delete is {}", absolutePath, delete);
            }
        }
        return null;
    }

    // FIXME 改成 serverless
    public List<ContentDTO> postLocalOcrService(String path) {
        String request = "path=" + path;
        String response = HttpUtils.postFormUrlEncoder("http://localhost:18999/ocr",
                request,
                Map.of("ContentType", "application/x-www-form-urlencoded;charset=UTF-8"));
        return JSON.parseArray(response, ContentDTO.class);
    }

}

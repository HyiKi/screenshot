package com.hyiki.screenshot.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 文本内容
 */
@Data
@Builder
public class ContentDTO {

    /**
     * 坐标 left top -> left bottom -> right bottom -> right top
     */
    private List<CoordinateDTO> coordinates;

    /**
     * 文本
     */
    private String text;

    @Data
    @AllArgsConstructor
    public static class CoordinateDTO {

        private BigDecimal x;

        private BigDecimal y;

    }

}

package com.fangcai.es.common.dto;

import com.fangcai.es.common.enums.FilterTimeTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author MouFangCai
 * @date 2020/6/7 20:33
 * @description 过滤条件
 */
@Data
public class BlogFilterDto {

    private String filterAuthor;

    private List<String> filterTags;

    private FilterTimeTypeEnum filterCreateAt;

    private Integer influenceGte;

    private Integer influenceLte;
}

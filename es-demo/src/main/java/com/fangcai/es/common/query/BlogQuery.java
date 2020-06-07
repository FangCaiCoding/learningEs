package com.fangcai.es.common.query;

import com.fangcai.es.common.dto.BlogFilterDto;
import com.fangcai.es.common.dto.BlogSortDto;
import com.fangcai.es.common.enums.BlogQueryTypeEnum;
import lombok.Data;


/**
 * @author MouFangCai
 * @date 2020/6/7 19:03
 * @description 检索条件
 */
@Data
public class BlogQuery {

    /**
     * 用户检索关键字
     */
    private String keyword;

    /**
     * 检索类型
     */
    private BlogQueryTypeEnum queryType;

    /**
     * 排序条件及规则
     */
    private BlogSortDto sort;

    /**
     * 筛选条件
     */
    private BlogFilterDto filter;


}

package com.fangcai.es.common.dto;

import com.fangcai.es.common.enums.SortFieldEnum;
import com.fangcai.es.common.enums.SortTypeEnum;
import lombok.Data;

/**
 * @author MouFangCai
 * @date 2020/6/7 19:06
 * @description
 */
@Data
public class BlogSortDto {

    private SortFieldEnum sortField;

    private SortTypeEnum sortType;
}






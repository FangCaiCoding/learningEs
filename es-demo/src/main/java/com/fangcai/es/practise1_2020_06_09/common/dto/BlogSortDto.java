package com.fangcai.es.practise1_2020_06_09.common.dto;

import com.fangcai.es.practise1_2020_06_09.common.enums.SortFieldEnum;
import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author MouFangCai
 * @date 2020/6/7 19:06
 * @description
 */
@Data
public class BlogSortDto {

    private SortFieldEnum sortField;

    private SortOrder sortOrder;
}






package com.fangcai.es.common.dto;

import com.fangcai.es.common.enums.SortFieldEnum;
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






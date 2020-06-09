package com.fangcai.es.practise1_2020_06_09.common.response;

import lombok.Data;

import java.util.List;

/**
 * @author MouFangCai
 * @date 2020/6/7 17:31
 * @description 分页返回obj
 */
@Data
public class PageResponse<T> {

    private List<T> data;

    private int pageNum;

    private int pageSize;

    private long total;
}

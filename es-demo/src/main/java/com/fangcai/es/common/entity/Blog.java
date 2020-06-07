package com.fangcai.es.common.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author MouFangCai
 * @date 2020/6/7 14:34
 * @description
 */
@Data
public class Blog {

    private Integer id;

    private String author;

    /**
     * type:integer_range
     */
    private Map<String,Integer> influence;

    private String title;

    private String content;

    /**
     * type:keyword,但可以为多值:Array
     */
    private List<String> tag;

    private Integer vote;

    private Integer view;

    private Date createAt;
}

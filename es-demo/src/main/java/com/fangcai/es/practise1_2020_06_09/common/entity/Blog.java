package com.fangcai.es.practise1_2020_06_09.common.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author MouFangCai
 * @date 2020/6/7 14:34
 * @description
 */
@Getter
@Setter
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

    /**
     * format: yyyy-MM-dd HH:mm
     */
    private String createAt;
}

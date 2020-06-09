package com.fangcai.es.practise1_2020_06_09.common.enums;

/**
 * @author MouFangCai
 * @date 2020/6/7 20:41
 * @description 检索类型
 */
public enum BlogQueryTypeEnum {

    RESCORE,

    BOOST,

    BOOST_FILTER,

    CUSTOM_SORT;

    BlogQueryTypeEnum () {
    }
}

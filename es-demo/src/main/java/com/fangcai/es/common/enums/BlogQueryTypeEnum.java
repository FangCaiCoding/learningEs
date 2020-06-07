package com.fangcai.es.common.enums;

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

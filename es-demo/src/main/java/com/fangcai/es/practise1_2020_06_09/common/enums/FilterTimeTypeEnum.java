package com.fangcai.es.practise1_2020_06_09.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MouFangCai
 * @date 2020/6/7 19:13
 * @description
 */
public enum FilterTimeTypeEnum {

    TODAY("now/d"),
    WEEK("now-1w/d"),
    MONTH("now-1M/d"),
    QUARTER("now-3M/d");
    private String startTime;
    FilterTimeTypeEnum (String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime () {
        return startTime;
    }
}

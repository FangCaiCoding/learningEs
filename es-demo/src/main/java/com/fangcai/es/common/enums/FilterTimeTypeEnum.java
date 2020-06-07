package com.fangcai.es.common.enums;

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
    MONTH("now-1m/d"),
    QUARTER("now-3m/d");
    private String startTime;
    FilterTimeTypeEnum (String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime () {
        return startTime;
    }
}

package com.fangcai.es.common.enums;

/**
 * @author MouFangCai
 * @date 2020/6/7 19:13
 * @description
 */
public enum SortTypeEnum {
    ASC("asc"),
    DESC("desc");
    private String type;
    SortTypeEnum (String type) {
        this.type = type;
    }

    public String getType () {
        return type;
    }
}

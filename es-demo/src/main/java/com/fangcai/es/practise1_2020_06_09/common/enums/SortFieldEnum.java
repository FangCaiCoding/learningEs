package com.fangcai.es.practise1_2020_06_09.common.enums;

/**
 * @author MouFangCai
 * @date 2020/6/7 19:13
 * @description 排序
 */
public enum SortFieldEnum {
    CREATE_AT("createAt"),
    VOTE("vote"),
    VIEW("view");
    private String field;
    SortFieldEnum (String field) {
        this.field = field;
    }

    public String getField () {
        return field;
    }
}

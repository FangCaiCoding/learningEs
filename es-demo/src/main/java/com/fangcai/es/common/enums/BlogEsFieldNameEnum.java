package com.fangcai.es.common.enums;

import java.util.List;

/**
 * @author MouFangCai
 * @date 2020/6/7 21:59
 * @description es中 blog index 的字段名
 */
public enum BlogEsFieldNameEnum {

    AUTHOR("author"),

    INFLUENCE("influence"),

    TITLE("title"),

    CONTENT("content"),

    TAG("tag"),

    VOTE("vote"),

    VIEW("view"),

    CREATEAT("createAt");

    private String fieldName;

    BlogEsFieldNameEnum (String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName () {
        return fieldName;
    }
}

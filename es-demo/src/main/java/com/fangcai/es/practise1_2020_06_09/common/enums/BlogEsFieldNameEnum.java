package com.fangcai.es.practise1_2020_06_09.common.enums;

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

    TAG_KEYWORD("tag.keyword"),

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

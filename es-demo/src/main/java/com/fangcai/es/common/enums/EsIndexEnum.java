package com.fangcai.es.common.enums;

/**
 * @author MouFangCai
 * @date 2020/6/7 22:21
 * @description
 */
public enum EsIndexEnum {

    BLOG("demo1_blog");

    private String indexName;

    EsIndexEnum (String indexName) {
        this.indexName = indexName;
    }

    public String getIndexName () {
        return indexName;
    }
}

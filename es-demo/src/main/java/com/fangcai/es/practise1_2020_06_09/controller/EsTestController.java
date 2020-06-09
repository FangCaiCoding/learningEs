package com.fangcai.es.practise1_2020_06_09.controller;


import com.fangcai.es.practise1_2020_06_09.common.entity.Blog;
import com.fangcai.es.practise1_2020_06_09.common.enums.EsIndexEnum;
import com.fangcai.es.practise1_2020_06_09.common.response.PageResponse;
import com.fangcai.es.practise1_2020_06_09.common.util.EsUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.rescore.QueryRescorerBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MouFangCai
 * @date 2020/6/9 15:07
 * @description
 */
@RestController
@RequestMapping ("es/test")
public class EsTestController{

    @Autowired
    private EsUtil esUtil;


    /**
     * 根据 title 、content 、tag 进行简单检索，使用rescore利用match_phrase重新算分排序；
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("case1")
    public PageResponse<Blog> case1 (@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        
        // 根据 title 、content 、tag 进行 match query
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery("系统学习ElasticSearch",
                "title","content","tag");
        searchSourceBuilder.query(multiMatchQuery);

        // 使用 reScore 利用 match_phrase 重新算分排
        MultiMatchQueryBuilder reScoreQuery = QueryBuilders.multiMatchQuery("系统学习ElasticSearch",
                "title","content","tag")
                .type(MultiMatchQueryBuilder.Type.PHRASE);
        QueryRescorerBuilder queryRescorerBuilder = new QueryRescorerBuilder(reScoreQuery);
        searchSourceBuilder.addRescorer(queryRescorerBuilder);

        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);
        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }


    /**
     * 提升 tag 的权重为3，title的权重为2，使用默认排序
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("case2")
    public PageResponse<Blog> case2 (@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 提升 tag 的权重为3，title的权重为2，使用默认排序
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.matchQuery("tag", "系统学习ElasticSearch").boost(3))
                .should(QueryBuilders.matchQuery("title", "系统学习ElasticSearch").boost(2))
                .should(QueryBuilders.matchQuery("content", "系统学习ElasticSearch"));
        searchSourceBuilder.query(boolQuery);

        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }


    /**
     * 在case2的基础上增加过滤条件：author、tag、createAt、influence
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("case3")
    public PageResponse<Blog> case3 (@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 提升 tag 的权重为3，title的权重为2，使用默认排序
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.matchQuery("tag", "系统学习ElasticSearch").boost(3))
                .should(QueryBuilders.matchQuery("title", "系统学习ElasticSearch").boost(2))
                .should(QueryBuilders.matchQuery("content", "系统学习ElasticSearch"));

        // 过滤
        boolQuery.filter(QueryBuilders.termQuery("author", "方才兄"));
        boolQuery.filter(QueryBuilders.termsQuery("tag.keyword", "ElasticSearch", "倒排序索引"));
        boolQuery.filter(QueryBuilders.rangeQuery("createAt").gte("now-3M/d").lte("now/d"));
        boolQuery.filter(QueryBuilders.rangeQuery("influence").gte(5).lte(15));

        searchSourceBuilder.query(boolQuery);

        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }



    /**
     * 在3的基础上用户指定排序条件：createAt、vote、view
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("case4")
    public PageResponse<Blog> case4 (@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 通过 filterContext 查询，忽略评分，增加缓存的可能性，提高查询性能
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery("系统学习ElasticSearch",
                "title","content","tag");
        boolQuery.filter(multiMatchQuery);

        // 过滤
        boolQuery.filter(QueryBuilders.termQuery("author", "方才兄"));
        boolQuery.filter(QueryBuilders.termsQuery("tag.keyword", "ElasticSearch", "倒排序索引"));
        boolQuery.filter(QueryBuilders.rangeQuery("createAt").gte("now-3M/d").lte("now/d"));
        boolQuery.filter(QueryBuilders.rangeQuery("influence").gte(5).lte(15));

        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.sort("view", SortOrder.DESC);
        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }

}

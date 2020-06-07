package com.fangcai.es.service.imp;

import com.alibaba.fastjson.JSON;
import com.fangcai.es.common.dto.BlogFilterDto;
import com.fangcai.es.common.entity.Blog;
import com.fangcai.es.common.enums.BlogEsFieldNameEnum;
import com.fangcai.es.common.enums.BlogQueryTypeEnum;
import com.fangcai.es.common.enums.EsIndexEnum;
import com.fangcai.es.common.query.BlogQuery;
import com.fangcai.es.common.response.PageResponse;
import com.fangcai.es.common.util.EsUtil;
import com.fangcai.es.service.BlogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Rescorer;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.rescore.QueryRescorerBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author MouFangCai
 * @date 2020/6/7 15:07
 * @description
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private EsUtil esUtil;

    @Override
    public PageResponse<Blog> listByQuery (BlogQuery query, Integer pageNum, Integer pageSize) {

        switch (query.getQueryType()){
            case RESCORE:
                return listByReScoreQuery(query, pageNum, pageSize);
            case BOOST:
                return listByBoostQuery(query, pageNum, pageSize);
            case BOOST_FILTER:
                return listByFilterQuery(query, pageNum, pageSize);
            case CUSTOM_SORT:
                return listBySortQuery(query, pageNum, pageSize);
            default:
                return null;
        }
    }


    private PageResponse<Blog> listByReScoreQuery (BlogQuery query, Integer pageNum, Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 根据 title 、content 、tag 进行 match query
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(query.getKeyword(),
                BlogEsFieldNameEnum.TITLE.getFieldName(),
                BlogEsFieldNameEnum.CONTENT.getFieldName(),
                BlogEsFieldNameEnum.TAG.getFieldName());
        searchSourceBuilder.query(multiMatchQuery);

        // 使用rescore利用 match_phrase 重新算分排
        MultiMatchQueryBuilder reScoreQuery = QueryBuilders.multiMatchQuery(query.getKeyword(),
                BlogEsFieldNameEnum.TITLE.getFieldName(),
                BlogEsFieldNameEnum.CONTENT.getFieldName(),
                BlogEsFieldNameEnum.TAG.getFieldName())
                .type(MultiMatchQueryBuilder.Type.PHRASE);
        QueryRescorerBuilder queryRescorerBuilder = new QueryRescorerBuilder(reScoreQuery);
        searchSourceBuilder.addRescorer(queryRescorerBuilder);

        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }


    private PageResponse<Blog> listByBoostQuery (BlogQuery query, Integer pageNum, Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 提升 tag 的权重为3，title的权重为2，使用默认排序
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(query.getKeyword(),
                BlogEsFieldNameEnum.TAG.getFieldName() + "^3",
                BlogEsFieldNameEnum.TITLE.getFieldName() + "^2",
                BlogEsFieldNameEnum.CONTENT.getFieldName());
        searchSourceBuilder.query(multiMatchQuery);

        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }


    private PageResponse<Blog> listByFilterQuery (BlogQuery query, Integer pageNum, Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 提升 tag 的权重为3，title的权重为2，使用默认排序
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(query.getKeyword(),
                BlogEsFieldNameEnum.TAG.getFieldName() + "^3",
                BlogEsFieldNameEnum.TITLE.getFieldName() + "^2",
                BlogEsFieldNameEnum.CONTENT.getFieldName());
        searchSourceBuilder.query(multiMatchQuery);

        // 过滤
        BlogFilterDto filter = query.getFilter();
        BoolQueryBuilder boolFilter = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(filter.getFilterAuthor())) {
            boolFilter.filter(QueryBuilders.termQuery(BlogEsFieldNameEnum.AUTHOR.getFieldName(),
                    filter.getFilterAuthor()));
        }
        if (! filter.getFilterTags().isEmpty()) {
            boolFilter.filter(QueryBuilders.termsQuery(BlogEsFieldNameEnum.TAG.getFieldName(),
                    filter.getFilterTags()));
        }
        if (filter.getFilterCreateAt() != null) {
            boolFilter.filter(QueryBuilders.rangeQuery(BlogEsFieldNameEnum.CREATEAT.getFieldName())
                    .gte(filter.getFilterCreateAt().getStartTime()).lte("now/d"));
        }
        if (filter.getInfluenceGte() != null) {
            boolFilter.filter(QueryBuilders.rangeQuery(BlogEsFieldNameEnum.INFLUENCE.getFieldName())
            .gte(filter.getInfluenceGte()));
        }
        if (filter.getInfluenceLte() != null) {
            boolFilter.filter(QueryBuilders.rangeQuery(BlogEsFieldNameEnum.INFLUENCE.getFieldName())
                    .lte(filter.getInfluenceLte()));
        }
        boolFilter.filter(boolFilter);
        searchSourceBuilder.query(boolFilter);

        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }


    private PageResponse<Blog> listBySortQuery (BlogQuery query, Integer pageNum, Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 通过 filterContext 查询，忽略评分，增加缓存的可能性，提高查询性能
        BlogFilterDto filter = query.getFilter();
        BoolQueryBuilder boolFilter = QueryBuilders.boolQuery();
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(query.getKeyword(),
                BlogEsFieldNameEnum.TAG.getFieldName(),
                BlogEsFieldNameEnum.TITLE.getFieldName(),
                BlogEsFieldNameEnum.CONTENT.getFieldName());
        boolFilter.filter(multiMatchQuery);
        if (StringUtils.isNotBlank(filter.getFilterAuthor())) {
            boolFilter.filter(QueryBuilders.termQuery(BlogEsFieldNameEnum.AUTHOR.getFieldName(),
                    filter.getFilterAuthor()));
        }
        if (! filter.getFilterTags().isEmpty()) {
            boolFilter.filter(QueryBuilders.termsQuery(BlogEsFieldNameEnum.TAG.getFieldName(),
                    filter.getFilterTags()));
        }
        if (filter.getFilterCreateAt() != null) {
            boolFilter.filter(QueryBuilders.rangeQuery(BlogEsFieldNameEnum.CREATEAT.getFieldName())
                    .gte(filter.getFilterCreateAt().getStartTime()).lte("now/d"));
        }
        if (filter.getInfluenceGte() != null) {
            boolFilter.filter(QueryBuilders.rangeQuery(BlogEsFieldNameEnum.INFLUENCE.getFieldName())
                    .gte(filter.getInfluenceGte()));
        }
        if (filter.getInfluenceLte() != null) {
            boolFilter.filter(QueryBuilders.rangeQuery(BlogEsFieldNameEnum.INFLUENCE.getFieldName())
                    .lte(filter.getInfluenceLte()));
        }
        boolFilter.filter(boolFilter);
        searchSourceBuilder.query(boolFilter);


        searchSourceBuilder.sort(query.getSort().getSortField().getField(), query.getSort().getSortOrder());
        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }


    public static void main (String[] args) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery("dfd",
                BlogEsFieldNameEnum.TITLE.getFieldName(),
                BlogEsFieldNameEnum.CONTENT.getFieldName(),
                BlogEsFieldNameEnum.TAG.getFieldName());
        searchSourceBuilder.query(multiMatchQuery);
        System.out.println(JSON.toJSONString(multiMatchQuery));
        System.out.println(22222222);
        QueryRescorerBuilder queryRescorerBuilder = new QueryRescorerBuilder(
                multiMatchQuery.type(MultiMatchQueryBuilder.Type.PHRASE));
        System.out.println(JSON.toJSONString(multiMatchQuery));

        searchSourceBuilder.addRescorer(queryRescorerBuilder);

        System.out.println(JSON.toJSONString(searchSourceBuilder.query()));
        System.out.println("1111111111111");
        System.out.println(JSON.toJSONString(queryRescorerBuilder));
    }
}

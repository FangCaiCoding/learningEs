package com.fangcai.es.practise1_2020_06_09.service.imp;


import com.fangcai.es.practise1_2020_06_09.common.dto.BlogFilterDto;
import com.fangcai.es.practise1_2020_06_09.common.entity.Blog;
import com.fangcai.es.practise1_2020_06_09.common.enums.BlogEsFieldNameEnum;
import com.fangcai.es.practise1_2020_06_09.common.enums.EsIndexEnum;
import com.fangcai.es.practise1_2020_06_09.common.query.BlogQuery;
import com.fangcai.es.practise1_2020_06_09.common.response.PageResponse;
import com.fangcai.es.practise1_2020_06_09.common.util.EsUtil;
import com.fangcai.es.practise1_2020_06_09.service.BlogService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.rescore.QueryRescorerBuilder;
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
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.matchQuery(BlogEsFieldNameEnum.TAG.getFieldName(),
                query.getKeyword()).boost(3))
                .should(QueryBuilders.matchQuery(BlogEsFieldNameEnum.TITLE.getFieldName(),
                        query.getKeyword()).boost(2))
                .should(QueryBuilders.matchQuery(BlogEsFieldNameEnum.CONTENT.getFieldName(),
                        query.getKeyword()));
        searchSourceBuilder.query(boolQuery);

        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }


    private PageResponse<Blog> listByFilterQuery (BlogQuery query, Integer pageNum, Integer pageSize) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 提升 tag 的权重为3，title的权重为2，使用默认排序
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.matchQuery(BlogEsFieldNameEnum.TAG.getFieldName(),
                query.getKeyword()).boost(3))
                .should(QueryBuilders.matchQuery(BlogEsFieldNameEnum.TITLE.getFieldName(),
                        query.getKeyword()).boost(2))
                .should(QueryBuilders.matchQuery(BlogEsFieldNameEnum.CONTENT.getFieldName(),
                        query.getKeyword()));

        // 过滤
        BlogFilterDto filter = query.getFilter();
        if (StringUtils.isNotBlank(filter.getFilterAuthor())) {
            boolQuery.filter(QueryBuilders.termQuery(BlogEsFieldNameEnum.AUTHOR.getFieldName(),
                    filter.getFilterAuthor()));
        }
        if (filter.getFilterTags() != null && ! filter.getFilterTags().isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery(BlogEsFieldNameEnum.TAG_KEYWORD.getFieldName(),
                    filter.getFilterTags()));
        }
        if (filter.getFilterCreateAt() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery(BlogEsFieldNameEnum.CREATEAT.getFieldName())
                    .gte(filter.getFilterCreateAt().getStartTime()).lte("now/d"));
        }
        if (filter.getInfluenceGte() != null && filter.getInfluenceLte() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery(BlogEsFieldNameEnum.INFLUENCE.getFieldName())
            .gte(filter.getInfluenceGte()).lte(filter.getInfluenceLte()));
        }

        searchSourceBuilder.query(boolQuery);

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
        if (filter.getFilterTags() != null && ! filter.getFilterTags().isEmpty()) {
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
        searchSourceBuilder.query(boolFilter);

        searchSourceBuilder.sort(query.getSort().getSortField().getField(), query.getSort().getSortOrder());
        // 分页
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.size(pageSize).from(from);

        return esUtil.search(EsIndexEnum.BLOG.getIndexName(), searchSourceBuilder,
                Blog.class, pageNum, pageSize);
    }

}

package com.fangcai.es.service.imp;

import com.fangcai.es.common.entity.Blog;
import com.fangcai.es.common.enums.BlogQueryTypeEnum;
import com.fangcai.es.common.query.BlogQuery;
import com.fangcai.es.common.response.PageResponse;
import com.fangcai.es.common.util.EsUtil;
import com.fangcai.es.service.BlogService;
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

        return null;
    }

    private PageResponse<Blog> listByBoostQuery (BlogQuery query, Integer pageNum, Integer pageSize) {
        return null;
    }

    private PageResponse<Blog> listByFilterQuery (BlogQuery query, Integer pageNum, Integer pageSize) {
        return null;
    }

    private PageResponse<Blog> listBySortQuery (BlogQuery query, Integer pageNum, Integer pageSize) {
        return null;
    }
}

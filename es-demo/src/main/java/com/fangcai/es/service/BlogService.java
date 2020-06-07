package com.fangcai.es.service;


import com.fangcai.es.common.entity.Blog;
import com.fangcai.es.common.query.BlogQuery;
import com.fangcai.es.common.response.PageResponse;

/**
 * @author MouFangCai
 * @date 2020/6/7 15:06
 * @description
 */
public interface BlogService {

    PageResponse<Blog> listByQuery (BlogQuery query, Integer pageNum, Integer pageSize);
}

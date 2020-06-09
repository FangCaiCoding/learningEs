package com.fangcai.es.practise1_2020_06_09.service;


import com.fangcai.es.practise1_2020_06_09.common.entity.Blog;
import com.fangcai.es.practise1_2020_06_09.common.query.BlogQuery;
import com.fangcai.es.practise1_2020_06_09.common.response.PageResponse;

/**
 * @author MouFangCai
 * @date 2020/6/7 15:06
 * @description
 */
public interface BlogService {

    PageResponse<Blog> listByQuery (BlogQuery query, Integer pageNum, Integer pageSize);
}

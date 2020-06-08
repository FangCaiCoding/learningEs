package com.fangcai.es.controller;

import com.fangcai.es.common.entity.Blog;
import com.fangcai.es.common.enums.EsIndexEnum;
import com.fangcai.es.common.query.BlogQuery;
import com.fangcai.es.common.response.PageResponse;
import com.fangcai.es.common.response.ResponseMsg;
import com.fangcai.es.common.util.EsUtil;
import com.fangcai.es.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author MouFangCai
 * @date 2020/6/7 15:05
 * @description 博客搜索系统
 */
@RestController
@RequestMapping ("es/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    /**
     * 博客检索
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("listByQuery")
    public ResponseMsg listByQuery(@RequestBody BlogQuery query,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {

       PageResponse<Blog> page = blogService.listByQuery(query, pageNum, pageSize);
       return new ResponseMsg(page);
    }




    @Autowired
    private EsUtil esUtil;


    @PostMapping("addBlog")
    public ResponseMsg addBlog(@RequestBody Blog blog) {
        Boolean b = esUtil.addDocToEs(blog, EsIndexEnum.BLOG.getIndexName());
        return new ResponseMsg(b);
    }

    @PutMapping("updateBlog")
    public ResponseMsg updateBlog(@RequestBody Blog blog) {
        Boolean b = esUtil.updateDocToEs(blog, EsIndexEnum.BLOG.getIndexName());
        return new ResponseMsg(b);
    }


    @DeleteMapping("deleteBlog")
    public ResponseMsg deleteBlog(@RequestParam Integer deleteId) {
        Boolean b = esUtil.deleteDocToEs(deleteId, EsIndexEnum.BLOG.getIndexName());
        return new ResponseMsg(b);
    }

}

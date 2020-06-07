package com.fangcai.es.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangcai.es.common.exception.EsDemoException;
import com.fangcai.es.common.response.PageResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MouFangCai
 * @date 2019/12/9 10:52
 * @description
 */
@Component
public class EsUtil {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

  

    @Autowired
    private RestHighLevelClient esClient;

    private static int limit = 3;


    private String getESId(Object obj) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(obj));
        Object id = jsonObject.get("id");
        return JSON.toJSONString(id);
    }

    /**
     * 搜索
     * @param index
     * @param searchSourceBuilder
     * @param clazz
     * @param pageNum
     * @param pageSize
     * @return PageResponse<T>
     */
    public <T> PageResponse<T> search(String index, SearchSourceBuilder searchSourceBuilder, Class<T> clazz, Integer pageNum, Integer pageSize){
        SearchSourceBuilder s = new SearchSourceBuilder();

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        PageResponse<T> pageResponse = null;
        try {
            response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            pageResponse = new PageResponse<>();
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotal(response.getHits().getTotalHits().value);
            List<T> dataList = new ArrayList<>();
            SearchHits hits = response.getHits();
            for(SearchHit hit : hits){
                dataList.add(JSONObject.parseObject(hit.getSourceAsString(), clazz));
            }
            pageResponse.setData(dataList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new EsDemoException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute searching,because of " + e.getMessage());
        }
        return pageResponse;
    }


    /**
     * 聚合
     * @param index
     * @param searchSourceBuilder
     * @param aggName 聚合名
     * @param pageNum
     * @param pageSize
     * @return Map<Integer, Long>  key:aggName   value: doc_count
     */
    public Map<Integer, Long> search(String index, SearchSourceBuilder searchSourceBuilder,
                                     String aggName, Integer pageNum, Integer pageSize){
        SearchRequest searchRequest = new SearchRequest(index);
        
        searchRequest.source(searchSourceBuilder);
        Map<Integer, Long> responseMap = new HashMap<>(0);
        try {
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            Terms terms = aggregations.get(aggName);
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            responseMap = new HashMap<>(buckets.size());
            Map<Integer, Long> finalResponseMap = responseMap;
            buckets.forEach(bucket-> {
                finalResponseMap.put(bucket.getKeyAsNumber().intValue(), bucket.getDocCount());
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new EsDemoException(String.valueOf(HttpStatus.BAD_REQUEST),
                    "error to execute aggregation searching,because of " + e.getMessage());
        }
        return responseMap;
    }





    /**
     *  新增文档
     * @param obj
     * @param index
     * @return
     */
    public Boolean addDocToEs(Object obj, String index){

        try {
            IndexRequest indexRequest = new IndexRequest(index, getESId(obj))
                    .source(JSON.toJSONString(obj), XContentType.JSON);
            int times = 0;
            while (times < limit) {
                IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);

                if (indexResponse.status().equals(RestStatus.CREATED) || indexResponse.status().equals(RestStatus.OK)) {
                    return true;
                } else {
                    logger.info(JSON.toJSONString(indexResponse));
                    times++;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Object = {}, index = {}, id = {} , exception = {}", obj, index, getESId(obj) , e.getMessage());
            throw new EsDemoException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute add doc,because of " + e.getMessage());
        }

    }


    /**
     *  更新文档
     * @param obj
     * @param index
     * @return
     */
    public Boolean updateDocToEs(Object obj, String index){
        try {
            UpdateRequest request = new UpdateRequest(index,  getESId(obj))
                    .doc(JSON.toJSONString(obj), XContentType.JSON);

            int times = 0;
            while (times < limit) {
                UpdateResponse update = esClient.update(request, RequestOptions.DEFAULT);

                if (update.status().equals(RestStatus.OK)) {
                    return true;
                } else {
                    logger.info(JSON.toJSONString(update));
                    times++;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Object = {}, index = {},exception = {}", obj, index, e.getMessage());
            throw new EsDemoException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute update doc,because of " + e.getMessage());
        }
    }


    /**
     *  删除文档
     * @param index
     * @param id
     * @return
     */
    public Boolean deleteDocToEs(String index, Integer id) {
        try {
            DeleteRequest request = new DeleteRequest(index, id.toString());

            int times = 0;
            while (times < limit) {
                DeleteResponse delete = esClient.delete(request, RequestOptions.DEFAULT);

                if (delete.status().equals(RestStatus.OK)) {
                    return true;
                } else {
                    logger.info(JSON.toJSONString(delete));
                    times++;
                }
            }

            return false;
        } catch (Exception e) {
            logger.error("index = {}, id = {} , exception = {}", index, id , e.getMessage());
            throw new EsDemoException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute update doc,because of " + e.getMessage());
        }
    }


    /**
     * 批量插入
     *
     * @param array 数据集合
     * @param index
     * @return
     */
    public Boolean batchAddToEs(JSONArray array, String index) {

        try {
            BulkRequest request = new BulkRequest();
            for (Object obj : array) {
                IndexRequest indexRequest = new IndexRequest(index,getESId(obj))
                        .source(JSON.toJSONString(obj), XContentType.JSON);
                request.add(indexRequest);
            }
            BulkResponse bulk = esClient.bulk(request, RequestOptions.DEFAULT);

            return bulk.status().equals(RestStatus.OK);
        } catch (Exception e) {
            logger.error("index = {}, exception = {}", index, e.getMessage());
            throw new EsDemoException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute batch add doc,because of " + e.getMessage());
        }
    }


    /**
     * 批量更新
     *
     * @param index
     * @return
     */
    public Boolean batchUpdateToEs(JSONArray array, String index){
        try {
            BulkRequest request = new BulkRequest();
            for (int i = 0; i < array.size(); i++) {
                Object obj = array.get(i);
                UpdateRequest updateRequest = new UpdateRequest(index,  getESId(obj))
                        .doc(JSON.toJSONString(obj), XContentType.JSON);
                request.add(updateRequest);
            }
            BulkResponse bulk = esClient.bulk(request, RequestOptions.DEFAULT);

            return bulk.status().equals(RestStatus.OK);
        } catch (Exception e) {
            logger.error("index = {}, exception = {}", index, e.getMessage());
            throw new EsDemoException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute batch update doc,because of " + e.getMessage());
        }
    }

    /**
     * 批量删除
     *
     * @param index
     * @return
     */
    public Boolean batchDeleteToEs(List<Integer> deleteIds, String index){
        try {
            BulkRequest request = new BulkRequest();
            for (Integer deleteId : deleteIds) {
                DeleteRequest deleteRequest = new DeleteRequest(index, deleteId.toString());
                request.add(deleteRequest);
            }
            BulkResponse bulk = esClient.bulk(request, RequestOptions.DEFAULT);

            return bulk.status().equals(RestStatus.OK);
        } catch (Exception e) {
            logger.error("index = {}, exception = {}", index, e.getMessage());
            throw new EsDemoException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute batch update doc,because of " + e.getMessage());
        }
    }
}

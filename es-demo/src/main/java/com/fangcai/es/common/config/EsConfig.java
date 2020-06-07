package com.fangcai.es.common.config;



import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


/**
 * @author MouFangCai
 * @date 2019/12/6 10:44
 * @description
 */
@Configuration
public class EsConfig implements FactoryBean<RestHighLevelClient>, InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String SCHEME = "http";

    private RestHighLevelClient restHighLevelClient;

    @Value ("${spring.data.elasticsearch.nodes}")
    private String nodes;


    /**
     * 控制Bean的实例化过程
     *
     * @return
     */
    @Override
    public RestHighLevelClient getObject() {
        return restHighLevelClient;
    }

    /**
     * 获取接口返回的实例的class
     *
     * @return
     */
    @Override
    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public void destroy() {
        try {
            if (null != restHighLevelClient) {
                restHighLevelClient.close();
            }
        } catch (final Exception e) {
            logger.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() {
        restHighLevelClient = buildClient();
    }

    private RestHighLevelClient buildClient() {
        try {
            String[] hosts = nodes.split(",");
            List<HttpHost> httpHosts = new ArrayList<>(hosts.length);
            for (String node : hosts) {
                HttpHost host = new HttpHost(
                        node.split(":")[0],
                        Integer.parseInt(node.split(":")[1]),
                        SCHEME);
                httpHosts.add(host);
            }
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(httpHosts.toArray(new HttpHost[0]))
            );
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return restHighLevelClient;
    }
}

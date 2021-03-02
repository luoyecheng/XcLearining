package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TetSearch {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    RestClient restClient;

    //搜索全部
    @Test
    public void testSearchAll() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //source源字段过滤 第一个string[]为包括的字段 第二个为不包括的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        long totalHits = hits.totalHits;
        //匹配度较高的前N个记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description); }
    }
    //分页搜索全部
    @Test
    public void testSearchByPage() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //分页参数
        //页码 每页记录数
        int page=1;
        int size=1;
        int form=(page-1)*size;
        searchSourceBuilder.from(form);
        searchSourceBuilder.size(size);
        //搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //source源字段过滤 第一个string[]为包括的字段 第二个为不包括的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        long totalHits = hits.totalHits;
        //匹配度较高的前N个记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description); }
    }

    @Test
    public void testTerm() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //分页参数
        //页码 每页记录数
        int page=1;
        int size=1;
        int form=(page-1)*size;
        searchSourceBuilder.from(form);
        searchSourceBuilder.size(size);
        //搜索全部
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //source源字段过滤 第一个string[]为包括的字段 第二个为不包括的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        long totalHits = hits.totalHits;
        //匹配度较高的前N个记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }
    //根据id精确查询
    @Test
    public void testTermPage() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //分页参数
        //页码 每页记录数
//        int page=1;
//        int size=1;
//        int form=(page-1)*size;
//        searchSourceBuilder.from(form);
//        searchSourceBuilder.size(size);
        //搜索
        String[] ids=new String[]{"1","2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //source源字段过滤 第一个string[]为包括的字段 第二个为不包括的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        long totalHits = hits.totalHits;
        //匹配度较高的前N个记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    //根据id精确查询
    @Test
    public void testMatch() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //分页参数
        //页码 每页记录数
//        int page=1;
//        int size=1;
//        int form=(page-1)*size;
//        searchSourceBuilder.from(form);
//        searchSourceBuilder.size(size);
        //搜索
       searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("80%"));
        //source源字段过滤 第一个string[]为包括的字段 第二个为不包括的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        long totalHits = hits.totalHits;
        //匹配度较高的前N个记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    @Test
    public void testMulti() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //搜索
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description").minimumShouldMatch("50%").field("name",10));
        //source源字段过滤 第一个string[]为包括的字段 第二个为不包括的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        long totalHits = hits.totalHits;
        //匹配度较高的前N个记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    @Test
    public void testBool() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(new String[]{"name","pic","studymodel"},new String[]{});
        //multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description") .minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name",10);
        //TermQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        long totalHits = hits.totalHits;
        //匹配度较高的前N个记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    @Test
    public void test() throws IOException {
        //搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(new String[]{"name","pic","studymodel"},new String[]{});
        //multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description") .minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name",10);

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel", "201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        long totalHits = hits.totalHits;
        //匹配度较高的前N个记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }
}

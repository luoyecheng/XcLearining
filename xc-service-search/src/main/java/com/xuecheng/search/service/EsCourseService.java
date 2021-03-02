package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsCourseService {
    @Value("${xuecheng.course.index}")
    private String es_index;
    @Value("${xuecheng.media.index}")
    private String es_media_index;
    @Value("${xuecheng.course.type}")
    private String es_type;
    @Value("${xuecheng.media.type}")
    private String es_media_type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    //课程搜索
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        //创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest(es_index);
        //设置搜索类型
        searchRequest.types(es_type);
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //过滤源字段
        String[] sourse_field_array = source_field.split(",");
        searchSourceBuilder.fetchSource(sourse_field_array,new String[]{});
        //搜索条件
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword()))
        {
            //匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name",
                    "teachplan", "description");
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            multiMatchQueryBuilder.field("name",10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //根据分类
        if(StringUtils.isNotEmpty(courseSearchParam.getMt()))
        {
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getSt()))
        {
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade()))
        {
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页参数
        if(page<=0)
            page=1;
        if(size<=0)
            size=12;
        int from=(page-1)*size;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        //设置高亮
//        HighlightBuilder highlightBuilder=new HighlightBuilder();
//        highlightBuilder.preTags("<font class='eslight'>");
//        highlightBuilder.preTags("</font>");
//        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
//        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse=null;
        QueryResult<CoursePub> queryResult=new QueryResult<>();
        List<CoursePub> list=new ArrayList<>();
        try {
            searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits();
            queryResult.setTotal(totalHits);
            SearchHit[] searchHits = hits.getHits();
            for(SearchHit searchHit:searchHits)
            {
                CoursePub coursePub=new CoursePub();
                //源文档
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                String name = (String) sourceAsMap.get("name");
                //取出高亮字段
//                Map<String, HighlightField> highlight = searchHit.getHighlightFields();
//                if(highlight!=null)
//                {
//                    HighlightField name1 = highlight.get("name");
//                    if(name1!=null)
//                    {
//                        Text[] fragments = name1.fragments();
//                        StringBuffer sb=new StringBuffer();
//                        for(Text text:fragments)
//                        {
//                            sb.append(text);
//                        }
//                        name=sb.toString();
//                    }
//                }
                coursePub.setName(name);
                String pic= (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                String id= (String) sourceAsMap.get("id");
                coursePub.setId(id);
                Double price=null;
                try {
                    if(sourceAsMap.get("price")!=null )
                    {
                        price = (Double) sourceAsMap.get("price");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //coursePub.setPrice(price);
                Float price_old = null;
                try {
                    if(sourceAsMap.get("price_old")!=null )
                    {
                        price_old = Float.parseFloat((String) sourceAsMap.get("price_old"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                list.add(coursePub);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryResult.setList(list);
        QueryResponseResult<CoursePub> queryResponseResult=new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    //查询ES的客户端向ES请求查询索引信息
    public Map<String, CoursePub> getAll(String id) {
        //定义一个搜索请求对象
        SearchRequest searchRequest=new SearchRequest(es_index);
        //指定type
        searchRequest.types(es_type);
        //定义searchBuilder
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //使用termquery
        searchSourceBuilder.query(QueryBuilders.termQuery("id",id));

        searchRequest.source(searchSourceBuilder);
        //返回对象
        CoursePub coursePub=new CoursePub();
        Map<String,CoursePub> map=new HashMap<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] searchHits = hits.getHits();
            for(SearchHit searchHit:searchHits)
            {
                //获取源文档内容
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                String courseid = (String) sourceAsMap.get("id");
                coursePub.setId(courseid);
                String name= (String) sourceAsMap.get("name");
                coursePub.setName(name);
                String grade= (String) sourceAsMap.get("grade");
                coursePub.setGrade(grade);
                String charge= (String) sourceAsMap.get("charge");
                coursePub.setCharge(charge);
                String pic= (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                String description= (String) sourceAsMap.get("description");
                coursePub.setDescription(description);
                String teachplan= (String) sourceAsMap.get("teachplan");
                coursePub.setTeachplan(teachplan);
                map.put(courseid,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    //根据多个课程计划id查询课程媒体资料信息
    public QueryResponseResult<TeachplanMediaPub> getMedia(String[] teachplanIds) {
        //定义一个搜索对象
        SearchRequest searchRequest=new SearchRequest(es_media_index);
        //type
        searchRequest.types(es_media_type);
        //定义searchBuilder
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //使用termquery
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        //过滤源字段
        String[] split = media_source_field.split(",");
        searchSourceBuilder.fetchSource(split,new String[]{});
        searchRequest.source(searchSourceBuilder);
        List<TeachplanMediaPub> teachplanMediaPubList=new ArrayList<>();
        long total=0;
        //搜索请求
        try {
            //执行搜索
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] searchHits = hits.getHits();
            total=hits.getTotalHits();
            for(SearchHit searchHit:searchHits)
            {
                TeachplanMediaPub teachplanMediaPub=new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                teachplanMediaPubList.add(teachplanMediaPub);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //数据集合
        QueryResult<TeachplanMediaPub> queryResult=new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(total);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult=new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}

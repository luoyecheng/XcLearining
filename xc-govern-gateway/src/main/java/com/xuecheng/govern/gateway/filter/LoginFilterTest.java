package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//@Component
public class LoginFilterTest extends ZuulFilter {
    //过滤器类型
    @Override
    public String filterType() {
        /**
         * pre
         * routing
         * post
         * error
         */
        return "pre";
    }
    //过滤器序号 越小越优先
    @Override
    public int filterOrder() {
        return 0;
    }
    //是否有效
    @Override
    public boolean shouldFilter() {
        return true;
    }
    //内容
    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        //得到request
        HttpServletRequest request = currentContext.getRequest();
        //得到response
        HttpServletResponse response = currentContext.getResponse();
        //得到Authorization头
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization))
        {
            //拒绝访问
            currentContext.setSendZuulResponse(false);
            //设置回复 响应代码
            currentContext.setResponseStatusCode(200);
            //构建响应信息
            ResponseResult responseResult=new ResponseResult(CommonCode.UNAUTHENTICATED);
            //转成json
            String jsonString = JSON.toJSONString(responseResult);
            currentContext.setResponseBody(jsonString);
            //设置contenttype
            response.setContentType("application/json;charset=utf-8");
            return null;
        }
        return null;
    }
}

package com.xuecheng.framework.domain.cms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryPageRequest {
    //站点Id
    @ApiModelProperty("站点Id")
    private String siteId;
    //页面Id
    @ApiModelProperty("页面Id")
    private String pageId;
    //页面名称
    @ApiModelProperty("页面名字")
    private String pageName;
    //别名
    @ApiModelProperty("别名")
    private String pageAliase;
    //模板
    @ApiModelProperty("模板Id")
    private String templateId;
}

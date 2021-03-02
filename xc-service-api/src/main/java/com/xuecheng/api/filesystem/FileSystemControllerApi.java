package com.xuecheng.api.filesystem;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "图片系统管理",description = "图片系统管理")
public interface FileSystemControllerApi  {
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata);
}



package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class FileSystemService {
    private static final Logger LOGGER= (Logger) LoggerFactory.getLogger(FileSystemService.class);
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;
    @Autowired
    FileSystemRepository fileSystemRepository;
    //加载fdfs配置
    private void initFdfsConfig()
    {
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
    }
    //上传文件
    public UploadFileResult upload(MultipartFile file, String filetag, String businesskey, String metadata){
        if(file==null)
        {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //上传文件到fdfs
        String fileid = fdfs_upload(file);
        //创建文件信息对象
        FileSystem fileSystem=new FileSystem();
        //文件id
        fileSystem.setFileId(fileid);
        //文件在文件系统的路径
        fileSystem.setFilePath(fileid);
        //业务标识
        fileSystem.setBusinesskey(businesskey);
        //标签
        fileSystem.setFiletag(filetag);
        //元数据
        if(StringUtils.isNotEmpty(metadata))
        {
            try{
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        //名称
        fileSystem.setFileName(file.getOriginalFilename());
        //大小
        fileSystem.setFileSize(file.getSize());
        //文件类型
        fileSystem.setFileType(file.getContentType());
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    //上传文件到fdfs 返回文件id
    public String fdfs_upload(MultipartFile file)
    {
        try {
            //加载配置
            initFdfsConfig();
            //创建tracker client
            TrackerClient trackerClient=new TrackerClient();
            //获取trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storage client
            StorageClient1 storageClient1=new StorageClient1(trackerServer,storeStorage);
            //上传文件  获取文件字节
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String extendName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String file1 = storageClient1.upload_file1(bytes, extendName, null);
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

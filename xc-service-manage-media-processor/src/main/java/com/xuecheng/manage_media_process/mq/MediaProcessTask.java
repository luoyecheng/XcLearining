package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MediaProcessTask {
    @Autowired
    MediaFileRepository mediaFileRepository;
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;
    @Value("${xc-service-manage-media.video-location}")
    String server_path;
    //接收视频处理信息进行视频处理
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    private void receiveProcessTask(String msg){
        //1、解析消息内容 得到mediaid
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        //2、拿mediaid查询数据库
        Optional<MediaFile> optional =mediaFileRepository.findById(mediaId);
        if(!optional.isPresent())
        {
            return;
        }
        MediaFile mediaFile = optional.get();
        String fileType = mediaFile.getFileType();
        if(!fileType.equals("avi"))
        {
            mediaFile.setProcessStatus("303004");//无需处理
            mediaFileRepository.save(mediaFile);
            return;
        }
        else
        {
            //需要处理
            mediaFile.setProcessStatus("303001");//处理中
            mediaFileRepository.save(mediaFile);
        }
        //3、使用工具类将avi生成mp4
        //待处理视频文件路径
        String video_path= server_path +mediaFile.getFilePath()+mediaFile.getFileName();
        //生成MP4文件名字
        String mp4_name=mediaFile.getFileId()+".mp4";
        //生成MP4文件路径
        String mp4folder_path=server_path+mediaFile.getFilePath();
        Mp4VideoUtil mp4VideoUtil=new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        String result=mp4VideoUtil.generateMp4();
        if(result==null||!result.equals("success")){
            //处理失败
            mediaFile.setProcessStatus("303003");
            //记录失败原因
            MediaFileProcess_m3u8 mediaFileProcess_m3u8=new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //4、将mp4生成m3u8 记录到数据库
        String mp4_video_path=server_path+mediaFile.getFilePath()+mp4_name;
        String m3u8_name=mediaFile.getFileId()+".m3u8";
        String m3u8_path=server_path+mediaFile.getFilePath()+"hls/";
        HlsVideoUtil hlsVideoUtil=new HlsVideoUtil(ffmpeg_path,mp4_video_path,m3u8_name,m3u8_path);
        String ans = hlsVideoUtil.generateM3u8();
        if(ans==null||!ans.equals("success"))
        {
            //处理失败
            mediaFile.setProcessStatus("303003");
            //记录失败原因
            MediaFileProcess_m3u8 mediaFileProcess_m3u8=new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //处理成功
        //获取ts列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        //定义对象
        MediaFileProcess_m3u8 mediaFileProcess_m3u8=new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        mediaFile.setProcessStatus("303002");
        //保存文件fileurl  视频播放相对路径
        String fileUrl=mediaFile.getFilePath()+"hls/"+m3u8_name;
        mediaFile.setFileUrl(fileUrl);
        mediaFileRepository.save(mediaFile);
    }

}

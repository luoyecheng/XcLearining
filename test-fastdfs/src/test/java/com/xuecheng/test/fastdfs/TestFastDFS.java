package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {
    //上传文件
    @Test
    public void test_upload()
    {
        //加载pro文件
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建客户端
            TrackerClient tc=new TrackerClient();
            TrackerServer ts=tc.getConnection();
            if(ts==null)
            {
                System.out.println("getConnection return null");
                return;
            }
            StorageServer ss = tc.getStoreStorage(ts);
            if(ss==null)
            {
                System.out.println("getStoreStorage return null");
            }
            //创建一个storage存储客户端
            StorageClient1 sc1=new StorageClient1(ts,ss);
            NameValuePair[] meta_list = null; //new NameValuePair[0];
            String item = "C:\\Users\\admin\\Desktop\\1.png";
            String fileid;
            fileid = sc1.upload_file1(item, "png", meta_list);
            System.out.println("Upload local file " + item + " ok, fileid=" + fileid);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    //下载文件
    @Test
    public void testDownloadFile() throws IOException, MyException {
        ClientGlobal.initByProperties("config/fastdfs‐client.properties");
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
        byte[] result = storageClient1.download_file1("group1/M00/00/01/wKhlQFrKBSOAW5AWAALcAg10vf4862.png");
        File file = new File("d:/1.png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(result);
        fileOutputStream.close(); }



}

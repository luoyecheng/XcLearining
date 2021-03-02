package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class TestFile {
    //测试文件分块
    @Test
    public void testChunk() throws IOException {
        //源文件
        File file=new File("D:\\ffmp_test\\lucene.avi");
        //目录
        String chunksFileFolder="D:\\ffmp_test\\chunks\\";
        //块文件大小
        long chunkFileSize=1*1024*1024;
        //块数
        long chunkFileNum=(long) Math.ceil(file.length()*1.0/chunkFileSize);
        //创建读文件
        RandomAccessFile randomAccessFile_read=new RandomAccessFile(file,"r");
        //缓冲区
        byte[] b=new byte[1024];
        for(int i=0;i<chunkFileNum;i++)
        {
            File chunkFile=new File(chunksFileFolder+i);
            //创建写对象
            RandomAccessFile randomAccessFile_write=new RandomAccessFile(chunkFile,"rw");
            int len=-1;
            while((len=randomAccessFile_read.read(b))!=-1)
            {
                randomAccessFile_write.write(b,0,len);
                if(chunkFile.length()>=chunkFileSize)
                    break;
            }
            randomAccessFile_write.close();
        }
        randomAccessFile_read.close();
    }

    //合并
    @Test
    public void test_1() throws IOException {
        //块文件目录
        String chunkFileFolderPath="D:\\ffmp_test\\chunks\\";
        //块文件目录对象
        File chunkFileFolder=new File(chunkFileFolderPath);
        //块文件列表
        File[] files = chunkFileFolder.listFiles();
        //排序 升序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName()))
                    return 1;
                return -1;
            }
        });
        //合并文件
        File merge=new File("D:\\ffmp_test\\lucene_merge.avi");
        boolean flag = merge.createNewFile();
        //创建写对象
        RandomAccessFile raf_write=new RandomAccessFile(merge,"rw");
        byte[] b=new byte[1024];
        for(File chunkFile:fileList)
        {
            RandomAccessFile raf_read=new RandomAccessFile(chunkFile,"r");
            int len=-1;
            while((len=raf_read.read(b))!=-1)
            {
                raf_write.write(b,0,len);
            }
            raf_read.close();
        }
        raf_write.close();

    }
}

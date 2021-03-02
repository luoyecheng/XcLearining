package com.xuecheng.speedclient.test;

public class Singelen {
    private volatile static Singelen unique;
    public static Singelen getSinggelen()
    {
        if(unique==null)
        {
            synchronized (Singelen.class)
            {
                if(unique==null)
                {
                    unique=new Singelen();
                }
            }
        }
        return unique;
    }
}

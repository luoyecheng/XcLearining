package com.xuecheng.speedclient.controller;

import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.speedclient.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/speed")
public class SecKillController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/nginx")
    public boolean nginx()
    {
        RestTemplate restTemplate=new RestTemplate();
        String test = restTemplate.getForObject("http://127.0.0.1/", String.class);
        if(test != null && test.contains("Welcome"))
            System.out.println("success!");
        return test != null && test.contains("Welcome");
    }


    //无锁
    @GetMapping("/seckill")
    public boolean seckill()
    {
        ResponseResult result = orderService.seckill();
        return result.isSuccess();
    }

    //悲观锁
    @GetMapping("/seckillPes")
    public ResponseResult seckillPessimisticLock()
    {
        try {
            return orderService.seckillPessimism();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //乐观锁
    @GetMapping("/seckillopt")
    public boolean OptimisticLock()
    {
        ResponseResult responseResult = orderService.seckillOptimistic();
        return responseResult.isSuccess();
    }

    //失败会重试锁
    @PostMapping("/seckilloptRetry")
    public void OptimisticLockRetry()
    {
        while (true){
            int i = orderService.seckillWithOptimistic();
            //如果卖光了 或者卖出成功跳出循环，否者一直循环，直到卖出去位置
            if(i==-1 || i>0){
                break;
            }
        }
    }

    @GetMapping(value = "/seckillRedis")
    public boolean seckillRedis(){
        ResponseResult responseResult = orderService.seckillWithRedis();
        return responseResult.isSuccess();
    }

}

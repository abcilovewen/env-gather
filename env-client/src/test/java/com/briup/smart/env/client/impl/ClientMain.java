package com.briup.smart.env.client.impl;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.config.Configuration;
import com.briup.smart.env.config.impl.ConfigurationImpl;
import org.junit.Test;

/**
 * @Author: Tjh
 * @create 2020/9/24 14:09
 */
public class ClientMain {

    @Test
    public void clientTest(){
        Configuration configuration = new ConfigurationImpl();
        //构建客户端对象
//        Client client = configuration.getClient();
        Client client = new ClientImpl();
        //构建采集模块对象
        Gather gather = new GatherImpl();
        try {
            client.send(gather.gather());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

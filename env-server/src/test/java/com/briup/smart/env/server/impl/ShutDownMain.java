package com.briup.smart.env.server.impl;

import com.briup.smart.env.server.Server;
import org.junit.Test;

/**
 * @Author: Tjh
 * @create 2020/9/26 15:57
 */
public class ShutDownMain {
    Server server = new ServerImplB();

    @Test
    public void shutdownTest(){
        try {
            server.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.briup.smart.env.client.impl;


import com.briup.smart.env.config.impl.ConfigurationImpl;
import com.briup.smart.env.entity.Environment;
import org.junit.Test;

import java.util.Collection;

/**
 * @Author: Tjh
 * @create 2020/9/23 14:22
 */
public class GatherTest {

    @Test
    public void gatherTest(){
        GatherImpl gather = new GatherImpl();
        try {
            Collection<Environment> gather1 = gather.gather();
            System.out.println(gather1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void storeTest(){
        GatherImpl gather = new GatherImpl();
        gather.storeIn();
    }

    @Test
    public void loadTest(){
        GatherImpl gather = new GatherImpl();
        gather.loadOut();
    }
}

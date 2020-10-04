package com.briup.smart.env.client.impl;

import com.briup.smart.env.config.impl.ConfigurationImpl;
import org.junit.Test;


/**
 * @Author: Tjh
 * @create 2020/9/25 15:35
 */
public class ConfigTest {
    @Test
    public void configTest(){
        ConfigurationImpl configuration = new ConfigurationImpl("/conf.xml");

    }
}

package com.briup.smart.env.server.impl;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.server.impl.ServerImpl;
import org.junit.Test;

import javax.xml.bind.annotation.XmlAnyAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

/**
 * @Author: Tjh
 * @create 2020/9/24 11:23
 */
public class ServerMain {

    @Test
    public void serverTest(){
        Server server = new ServerImpl();
        try {
            server.reciver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void serverTest1(){
        Server server = new ServerImplB();
        try {
            server.reciver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

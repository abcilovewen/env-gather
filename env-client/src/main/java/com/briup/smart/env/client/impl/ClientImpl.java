package com.briup.smart.env.client.impl;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.PropertiesAware;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

/**
 * @Author: Tjh
 * @create 2020/9/24 11:18
 */
public class ClientImpl implements Client, PropertiesAware {
    String serverIp;
    int port;
    @Override
    public void send(Collection<Environment> c) throws Exception {
        Gather gather = new GatherImpl();
        Socket socket = new Socket(serverIp,port);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(c);
        oos.flush();
        oos.close();
    }

    @Override
    public void init(Properties properties) throws Exception {
        System.out.println("Client impl init" + properties);
        serverIp = properties.getProperty("server-ip");
        port = Integer.parseInt(properties.getProperty("port"));
    }
}

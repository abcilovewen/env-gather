package com.briup.smart.env.server.impl;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Scanner;

/**
 * @Author: Tjh
 * @create 2020/9/24 11:23
 */
public class ServerImpl implements Server {
    ServerSocket server = null;
    int port = 8088;
    DBStore db = new DBStoreImpl();
    @Override
    public void reciver() throws Exception {
        try {
            server = new ServerSocket(port);
            while(true){
                Socket client = server.accept();
                //每接受到一个来自客户端的请求，分配一个线程去处理
                System.out.println(client.getRemoteSocketAddress() + "开始入库！");
                new Thread(()->{//run()方法里的动作
                    try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){
                        Object obj = null;
                        obj = ois.readObject();
                        if (obj != null){
                            Collection<Environment> coll = (Collection<Environment>)obj;
                            System.out.println("读取到客户端发送的"+ coll.size() + "条数据！");
                            //入库
                            db.saveDB(coll);
                            Thread.sleep(2000);
                            System.out.println(client.getRemoteSocketAddress() + "入库完成！");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void shutdown() throws Exception {

    }
}

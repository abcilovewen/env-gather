package com.briup.smart.env.server.impl;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Tjh
 * @create 2020/9/24 16:16
 */
public class ServerImplB implements Server {
    ServerSocket server = null;
    ServerSocket shutdownServer = null;
    int port = 8088;
    int shutdownPort = 8099;
    String server_ip = "127.0.0.1";
    DBStore db = new DBStoreImpl();
    ExecutorService service = null;
    {
        service = Executors.newCachedThreadPool();
    }
    @Override
    public void reciver() throws Exception {
        server = new ServerSocket(port);
        //接收一个关闭的信号就关闭服务器
        shutdownServer = new ServerSocket(shutdownPort);
        service.submit(()->{
            try(Socket socket = shutdownServer.accept()){
                //先关接收服务器的对象，使其抛出异常
                server.close();
                //然后线程池对象关闭，但是里面会结束后关闭
                service.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    shutdownServer.close();
                } catch (IOException e) {
                }
            }
        });
        while (true){
            Socket client = server.accept();
            //提交任务，处理客户端发送的请求，并将接受到的数据入库
            service.submit(()->{
                try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){
                    Object object = ois.readObject();
                    if (object != null){
                        Collection<Environment> coll = (Collection<Environment>) object;
                        System.out.println("读取到客户端发送的" + coll.size() + "条数据");
                        //入库
                        db.saveDB(coll);
                        Thread.sleep(10000);
                        System.out.println(client.getRemoteSocketAddress() + "入库完成");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void shutdown() throws Exception {
        try(Socket socket = new Socket(server_ip,shutdownPort)){

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

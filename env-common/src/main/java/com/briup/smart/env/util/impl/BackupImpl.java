package com.briup.smart.env.util.impl;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Backup;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Tjh
 * @create 2020/9/23 16:13
 */
public class BackupImpl implements Backup {
    public Object load(String fileName, boolean del) throws Exception {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))){
            File file = new File(fileName);
            Object object = ois.readObject();
            System.out.println("反序列化成功！");
            if (del == LOAD_REMOVE){
                if (file.exists()){
                    ois.close();
                    file.delete();
                    System.out.println("已删除！");
                }
            }
            return object;
        }
    }

    public void store(String fileName, Object obj, boolean append) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName, append))) {
            oos.writeObject(obj);
            oos.flush();
        }
    }
}


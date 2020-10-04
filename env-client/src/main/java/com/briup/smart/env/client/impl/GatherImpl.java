package com.briup.smart.env.client.impl;

import com.briup.smart.env.client.Gather;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.impl.BackupImpl;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import static com.briup.smart.env.util.Backup.*;

/**
 * @Author: Tjh
 * @create 2020/9/23 10:21
 */
public class GatherImpl implements Gather {

    Backup backup = new BackupImpl();

    public Collection<Environment> gather() throws Exception {
        ArrayList<Environment> arrayList = new ArrayList<>();

        //读取文件，获取文件中的每一行数据
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(
                new File("E:/smart_program/data-file"), "r")) {
            File file = new File("E:/smart_program/saveSeek");
            //获取之前一共采集到的字节个数
            long count = 0;
            //判断记录seek的文件是否存在，若存在，读取出来
            if (file.exists()){
                Object obj = backup.load("E:/smart_program/saveSeek", LOAD_REMOVE);
                if(obj != null){
                    count = (Long)obj;
                    System.out.println("读取点完成 : " + count);
                }
            }
            //根据读到的数据，进行解析，封装成一个个的Environment对象
            String s = "";
            //跳过之前已经采集过的环境数据
            randomAccessFile.seek(count);
            //测试

            while ((s = randomAccessFile.readLine()) != null) {
                String[] split = s.split("\\|");
//                String[] split1 = s.split("[|]");
                //如果不知道是\n\r,\n,\r的话，注释下列语句，用来判断
                if (split.length != 9) {
                    continue;
                }
                Environment environment = new Environment();
                environment.setSrcId(split[0]);//发送端Id
                environment.setDesId(split[1]);//树莓派系统Id
                environment.setDevId(split[2]);//实验箱区域模块id(1-8)
                environment.setCount((int) (Integer.parseInt(split[4])));//传感器个数
                environment.setCmd(split[5]);//发送指令标号 3表示接收数据 16表示发送命令
                environment.setStatus((int) (Integer.parseInt(split[7])));//状态 默认1表示成功
                environment.setGather_date(new Timestamp(Long.parseLong(split[8])));//采集时间
                environment.setSersorAddress(split[3]);//模块上传感器地址
                float data = Integer.parseInt(split[6].substring(0, 4), 16);
                    /*
                        1.如果传感器地址是16，代表两个环境数据参数，一个是温度，一个是湿度
                        //温度数据：(data*(0.00268127F)) - 46.85F
                        //湿度数据：(data * 0.00190735F) - 6
                        2.如果地址是256/1280，代表光照/二氧化碳数据产生
                    */
                switch (split[3]) {
                    case "16":
                        environment.setName("温度");//环境种类名称
                        Environment environment1 = (Environment) environment.clone();
                        //计算湿度、温度的值
                        float temperature = data;//温度
                        float humidity = Integer.parseInt(split[6].substring(4, 8), 16);//湿度
                        temperature = (temperature * (0.00268127F)) - 46.85F;
                        humidity = (humidity * 0.00190735F) - 6;
                        environment.setData(temperature);
                        environment1.setName("湿度");
                        environment1.setData(humidity);
                        arrayList.add(environment1);
//                        System.out.println(environment1);
                        break;
                    case "256":
                        environment.setName("光照");//环境种类名称
                        float illumination = data;
                        environment.setData(illumination);
                        break;
                    case "1280":
                        environment.setName("二氧化碳");//环境种类名称
                        float CO2 = data;
                        environment.setData(CO2);
                        break;
                    default:
                        System.out.println("该数据不存在");
                        break;
                }
                arrayList.add(environment);
//                System.out.println(environment);
                count += s.length() + 1;//一行读取的字节数
                //测试该文件是"\n","\r",还是"\n\r"
               /* if(arrayList.size() % 30000 == 0){
                    break;
                }*/
            }
            //记录下来，本次采集结束之后读取到的，所在文件的偏移位置
            System.out.println("读取到的字节个数：" + count);
            backup.store("E:/smart_program/saveSeek",count,STORE_OVERRIDE);
            System.out.println("储存点完成");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //将采集到的数据集合返回
        System.out.println("共采集了" + arrayList.size() + "条数据");
        return arrayList;
    }

    //序列化文件
    public void storeIn() {
        GatherImpl g = new GatherImpl();
        try {
            Collection<Environment> obj = g.gather();
            String fileName = "E:/smart_program/storeFile";
            backup.store(fileName, obj, STORE_APPEND);
            System.out.println("序列化文件成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //反序列化文件
    public void loadOut() {
        String fileName = "E:/smart_program/storeFile";
        try {
            Object obj = backup.load(fileName, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

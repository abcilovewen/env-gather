package com.briup.smart.env.server.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.DBStore;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

/**
 * @Author: Tjh
 * @create 2020/9/24 11:21
 */
public class DBStoreImpl implements DBStore {

    private String driverClass = "oracle.jdbc.driver.OracleDriver";
    private String url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
    private String user = "tjh";
    private String password = "tjh";

    @Override
    public void saveDB(Collection<Environment> c) throws Exception {
        conn_druid(c);
    }

    public void conn_druid(Collection<Environment> c) {
        DruidDataSource dataSource;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        int day = 0;

        //创建数据库连接池
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        //设置创建连接池时创建多少个连接
        dataSource.setInitialSize(5);
        //设置最大连接数，当从连接池获取到的连接超过了5个连接时会继续创建，超过了10个就需要等待
        dataSource.setMaxActive(10);

        /*//获取每月中天的数的另一种方法
        Calendar calendar = Calendar.getInstance();
        Timestamp timestamp = e.getGather_date();
        calendar.setTime(timestamp);
        int i = calendar.get(Calendar.DAY_OF_MONTHA);*/

        try {
            //获取连接
            conn = dataSource.getConnection();
            int count = 0;
            int preDay = 0;//声明一个变量，判断天数是否发生改变
            if (c != null) {
                for (Environment e : c) {
                    day = Integer.parseInt(sdf.format(e.getGather_date()));
                    if (day != preDay) {
                        if (preparedStatement != null) {
                            preparedStatement.executeBatch();//天数变了后，缓存区还有数据，将缓存区的剩余数据提交
                            preparedStatement.close();//提交完后关闭
                        }
                        String sql = "insert into E_DETAIL_" + day
                                + "(name,srcId,desId,devId,sersorAddress,count,cmd,status,data,gather_date)"
                                + " values (?,?,?,?,?,?,?,?,?,?)";
                        System.out.println("存入天数为 : " + day);
                        preparedStatement = conn.prepareStatement(sql);
                        preDay = day;
                    }
                    preparedStatement.setString(1, e.getName());
                    preparedStatement.setString(2, e.getSrcId());
                    preparedStatement.setString(3, e.getDesId());
                    preparedStatement.setString(4, e.getDevId());
                    preparedStatement.setString(5, e.getSersorAddress());
                    preparedStatement.setInt(6, e.getCount());
                    preparedStatement.setString(7, e.getCmd());
                    preparedStatement.setInt(8, e.getStatus());
                    preparedStatement.setFloat(9, e.getData());
                    preparedStatement.setTimestamp(10, e.getGather_date());
                    preparedStatement.addBatch();
                    count++;
                }
                System.out.println("共读到" + count + "条数据");
                if (preparedStatement != null){
                    preparedStatement.executeBatch();
                    preparedStatement.close();
                }
            } else {
                throw new RuntimeException("沒有读取到数据");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }
}

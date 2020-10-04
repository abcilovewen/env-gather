package com.briup.smart.env.server.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author: Tjh
 * @create 2020/9/24 17:27
 */
public class DBStoreMain {
    String driverClass = "oracle.jdbc.driver.OracleDriver";
    String url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
    String user = "tjh";
    String password = "tjh";

    //测试pdf上的疑惑的例子
    @Test
    public void ps_batch() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(10);
        try {
            conn = dataSource.getConnection();
            String sql = "insert into t_user (id,name,age) values (user_seq.nextval,?,?)";
            pstmt = conn.prepareStatement(sql);
            for (int i = 1; i < 10000; i++) {
                pstmt.setString(1, "tom" + i);
                pstmt.setInt(2, 15);
                pstmt.addBatch();
                if (i % 200 == 0) {
                    pstmt.executeBatch();
                }
            }
            pstmt.executeBatch();
            System.out.println("试验成功");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    //配置文件的方式连接数据库
    public void test_properties() {
        Connection conn = null;
        Properties properties = new Properties();
        InputStream is = DBStoreMain.class.getClassLoader().getResourceAsStream("druid.properties");
        try {
            properties.load(is);
            properties.forEach((k, v) -> System.out.println(k + " = " + v));
            //获取连接池对象
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            //获取连接
            conn = dataSource.getConnection();
            System.out.println(conn);
        } catch (IOException e) {
            e.printStackTrace();
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
        }
    }
}


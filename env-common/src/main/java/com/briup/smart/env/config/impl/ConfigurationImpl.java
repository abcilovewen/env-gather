package com.briup.smart.env.config.impl;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.config.Configuration;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;
import com.sun.jndi.toolkit.url.Uri;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: Tjh
 * @create 2020/9/27 11:49
 */
public class ConfigurationImpl implements Configuration {

    //声明一个集合容器，用来存放在解析xml过程中创建并且初始化好的对象
    Map<String,Object> container = new HashMap<>();
    public ConfigurationImpl(){
        System.out.println(this.getClass());
        URL uri = this.getClass().getResource("/conf.xml");
        this.parseXML(uri.toString());
    }

    public ConfigurationImpl(String path){
        this.parseXML(path);
    }

    //解析xml文件
    public void parseXML(String pathFile){
        SAXReader reader = new SAXReader();
        try {
            //获取Document(文档树)对象
            Document document = reader.read(this.getClass().getResource(pathFile));
            //获取根节点
            Element root = document.getRootElement();
            //获取各个标签
            List<Element> modules = root.elements();
            for (Element module : modules){
                //拿到标签名字，作为获取模块对象的名字
                String modelName = module.getName();
                //拿到标签上的class属性
                Attribute attribute = module.attribute("class");
                System.out.println("标签名字 : " + modelName);
                System.out.println("标签属性 : " + attribute.getValue());
                //拿到标签属性，并通过反射创建它的实例对象
                Object obj = Class.forName(attribute.getValue()).newInstance();
                //获取各个标签的子标签
                Properties properties = new Properties();
                List<Element> propsElement = module.elements();
                for (Element e : propsElement){
//                    System.out.println("Name : " + e.getName());
//                    System.out.println("Value : " + e.getText());
                    properties.setProperty(e.getName(),e.getText());//将获取到的属性值封装
                }
                //如果实例对象是PropertiesAware的实例对象的话,将它强转后，将获取属性后的值初始化
                //判断是否属于PropertiesAware接口类型，如果是，代表需要Configuration
                //将xml文件中配置的动态属性信息传递给该对象，进行初始化
                if (obj instanceof PropertiesAware){
                    PropertiesAware aware = (PropertiesAware)obj;
                    aware.init(properties);
                }
                //注意，这里是有顺序要求的（对应xml中的节点顺序），如果不想顾虑顺序问题，提前声明对象，维护在集合中
                //遍历集合时，再来设置属性，和configuration
                //判断是否属于ConfigurationAware类型，如果是，代表该对象需要对其他模块
                //对象进行操作，因此需要获取Configuration对象用来获取其他模块对象。
                if (obj instanceof ConfigurationAware){
                    ConfigurationAware aware = (ConfigurationAware)obj;
                    aware.setConfiguration(this);//极其重要！！！
                }
                //将获取到的标签名以及创建的实例对象放在集合容器中，方便后面声明对象。
                container.put(module.getName(),obj);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Log getLogger() throws Exception {
        return (Log)container.get("logger");
    }

    @Override
    public Server getServer() throws Exception {
        return (Server)container.get("server");
    }

    @Override
    public Client getClient() throws Exception {
        return (Client)container.get("client");
    }

    @Override
    public DBStore getDbStore() throws Exception {
        return (DBStore)container.get("dbStore");
    }

    @Override
    public Gather getGather() throws Exception {
        return (Gather)container.get("gather");
    }

    @Override
    public Backup getBackup() throws Exception {
        return (Backup)container.get("backup");
    }
}

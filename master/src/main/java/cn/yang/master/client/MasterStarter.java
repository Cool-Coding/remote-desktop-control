package cn.yang.master.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author Cool-Coding
 *         2018/7/25
 *Master启动器
 */
public class MasterStarter {
    public static void main(String[] args){
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("master-beans.xml");
        context.start();
    }
}

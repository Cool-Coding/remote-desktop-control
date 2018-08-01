package cn.yang.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class ServerStarter {
    public static void main(String[] args){
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("server-beans.xml");
        context.start();
    }
}

package cn.yang.puppet.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author Cool-Coding
 *         2018/7/25
 * Puppet启动器
 */
public class PuppetStarter {
    public static void main(String[] args){
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("puppet-beans.xml");
        context.start();
    }

}

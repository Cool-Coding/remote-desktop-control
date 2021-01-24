package cn.yang.master.client;

import cn.yang.master.client.ui.IMasterDesktop;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author Cool-Coding
 *         2018/7/25
 *Master启动器
 */
public class MasterStarter {

    public static void main(String[] args){
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("master-beans.xml");
        context.start();
        context.getBean(IMasterDesktop.class).lanuch();
    }
}

package cn.yang.puppet.client;

import cn.yang.puppet.client.ui.IReplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Cool-Coding
 *         2018/7/25
 * Puppet启动器
 */
public class PuppetStarter {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(PuppetStarter.class);

    public static void main(String[] args){
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("puppet-beans.xml");
        context.start();
        /*
         * 将连接服务器放在spring初始化bean后，有两个原因:
         * 1.防止阻塞spring初始化bean
         * 2.连接服务器后可能需要做出一些处理，处理中可能需要使用spring bean，而此时spring bean可能尚未
         *   初始化完成。例如cn.yang.puppet.client.commandhandler.AbstractPuppetCommandHandler类中
         *   静态成员变量generator
         */
        try {
            context.getBean(IReplay.class).connect();
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }

    }

}

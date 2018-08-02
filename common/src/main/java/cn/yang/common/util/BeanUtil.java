package cn.yang.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author cool-coding
 * 2018/7/27
 * 获取bean的工作类
 */
public class BeanUtil implements BeanFactoryAware {
    private static BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
      BeanUtil.beanFactory=beanFactory;

    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> aclass,String beanName){
        return (T)beanFactory.getBean(beanName);
    }

    public static <T> T getBean(Class<T> aclass){
        return (T)beanFactory.getBean(aclass);
    }

    public static <T> T getBean(Class<T> aclass,Object... args){
        return (T)beanFactory.getBean(aclass,args);
    }
}

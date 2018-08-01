package cn.yang.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author cool-coding
 * @date 2018/7/27
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
}

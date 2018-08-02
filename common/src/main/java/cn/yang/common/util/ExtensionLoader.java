package cn.yang.common.util;

import cn.yang.common.constant.ExceptionMessageConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ExtensionLoader {
    /**
     * 这里没有用ConcurrentHashMap，因为在代码中保证了同一时刻只会有一个线程进行put操作
     */
    private static final Map<Class,Object> SINGLE_OBJECT_MAP =new HashMap<>();

    /**
     * 加载单例对象
     * @param clazz 类字节码
     * @param <T>   类名
     * @return 单例对象
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadSingleObject(Class<T> clazz) throws IOException{
        Object o = SINGLE_OBJECT_MAP.get(clazz);
        if (o!=null){
            return (T)o;
        }

        synchronized (ExtensionLoader.class) {
            o = SINGLE_OBJECT_MAP.get(clazz);
            if (o!=null){
                return (T)o;
            }

            final ServiceLoader<T> load = ServiceLoader.load(clazz);
            final Iterator<T> iterator = load.iterator();
            while (iterator.hasNext()) {
                final T next = iterator.next();
                if (next != null) {
                    SINGLE_OBJECT_MAP.put(clazz, next);
                    break;
                }
            }
        }

        o = SINGLE_OBJECT_MAP.get(clazz);
        if (o==null){
            throw new IOException(clazz.getCanonicalName()+ExceptionMessageConstants.EXTENSION_NOT_FOUND);
        }
        return (T)o;
    }

    /**
     * 加载对象，每加载一次就产生一个新的实例对象
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadObject(Class<T> clazz){
        final ServiceLoader<T> load = ServiceLoader.load(clazz);
        final Iterator<T> iterator = load.iterator();
        while (iterator.hasNext()) {
            final T next = iterator.next();
            if (next != null) {
                SINGLE_OBJECT_MAP.put(clazz, next);
                break;
            }
        }
        return (T) SINGLE_OBJECT_MAP.get(clazz);
    }
}

package cn.yang.common.util;

import cn.yang.common.constant.ExceptionConstants;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.common.sequence.SequenceGenerator;

import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class SequenceGeneratorUtil {
    private static SequenceGenerator generator;

    public static synchronized SequenceGenerator getSequenceGenerator() throws IOException{
       if(generator!=null){
           return generator;
       }

        final ServiceLoader<SequenceGenerator> load = ServiceLoader.load(SequenceGenerator.class);
        final Iterator<SequenceGenerator> iterator = load.iterator();
        while (iterator.hasNext()) {
            final SequenceGenerator next = iterator.next();
            if (next != null) {
                generator = next;
                break;
            }
        }

        if (generator == null) {
            throw new IOException(ExceptionConstants.GENERATOR_NOT_FOUND);
        }

        return generator;
    }
}

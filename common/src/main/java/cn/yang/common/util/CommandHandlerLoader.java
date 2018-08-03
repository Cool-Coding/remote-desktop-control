package cn.yang.common.util;

import cn.yang.common.command.Commands;
import cn.yang.common.command.handler.ICommandHandler;
import cn.yang.common.constant.ExceptionMessageConstants;
import cn.yang.common.exception.CommandHandlerLoaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.yang.common.constant.ExceptionMessageConstants.*;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class CommandHandlerLoader {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlerLoader.class);

    private static final Map<Enum<Commands>,ICommandHandler> HANDLERS =new ConcurrentHashMap<>();

    private static final String COMMAND_HANDLER_PATH="META-INF/commandhandlers";


    /**
     * 根据命令获取对应的ICommandHandler对象
     * @param command 命令
     * @return  command handler
     */
    public static ICommandHandler getCommandHandler(Enum<Commands> command) throws CommandHandlerLoaderException {
        if(command==null){
            return null;
        }

        //查询缓存
        final ICommandHandler iCommandHandler = HANDLERS.get(command);
        if (iCommandHandler!=null){
            return iCommandHandler;
        }


        synchronized (CommandHandlerLoader.class) {
            //加载第一个为ICommandHandler类型的实现
            final ICommandHandler commandHandler = loadCommandHandler(command);
            HANDLERS.put(command, commandHandler);
            return commandHandler;
        }
    }

    private static ICommandHandler loadCommandHandler(Enum<Commands> command) throws CommandHandlerLoaderException {
        final ICommandHandler iCommandHandler = HANDLERS.get(command);
        if (iCommandHandler!=null){
            return iCommandHandler;
        }

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final InputStream resourceAsStream = classLoader.getResourceAsStream(COMMAND_HANDLER_PATH);
        if (resourceAsStream==null){
            LOGGER.error(COMMANDHANDLERS_FILE_NOT_FOUND);
            throw  new CommandHandlerLoaderException(COMMANDHANDLERS_FILE_NOT_FOUND);
        }

        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream, "utf-8"));
            String s;
            while(!StringUtils.isEmpty(s=bufferedReader.readLine())){
                final String[] split = s.split("=");
                if (split.length!=2){
                    LOGGER.error(COMMANDHANDLERS_FILE_CONFIG_ERROR);
                    throw new CommandHandlerLoaderException(COMMANDHANDLERS_FILE_CONFIG_ERROR);
                }

                String key=split[0];
                if (command.name().equals(key)) {
                    String value = split[1];
                    final Class<?> aClass = Class.forName(value);
                    final Class<?> inter = getSuperestInterface(aClass);
                    if (inter == null) {
                        throw new CommandHandlerLoaderException(COMMAND_HANDLER_ERROR);
                    }
                    return (ICommandHandler) aClass.newInstance();
                }
            }
            throw new CommandHandlerLoaderException(String.format("%s %s",command.name(), ExceptionMessageConstants.COMMAND_HANDLER_NOT_FOUND));
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw  new CommandHandlerLoaderException(e.getMessage(),e);
        }
    }

    private static Class<?> getSuperestInterface(Class aClass){
        //如果没有父类，则返回null
        if (aClass==null) {
            return null;
        }

        //获取类继承的接口
        final Class<?>[] interfaces = aClass.getInterfaces();

        //判断是否继承了接口ICommandHandler
        for(Class c:interfaces){
            if (c==ICommandHandler.class){
                return c;
            }
        }

        return getSuperestInterface(aClass.getSuperclass());
    }
}

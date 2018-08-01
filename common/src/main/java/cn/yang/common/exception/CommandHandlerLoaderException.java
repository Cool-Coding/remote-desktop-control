package cn.yang.common.exception;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class CommandHandlerLoaderException extends Exception {
    public CommandHandlerLoaderException(String msg){
        super(msg);
    }

    public CommandHandlerLoaderException(String msg, Exception e){
        super(msg,e);
    }
}

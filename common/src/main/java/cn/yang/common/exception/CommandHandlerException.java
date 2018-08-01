package cn.yang.common.exception;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class CommandHandlerException extends Exception {
    public CommandHandlerException(String msg){
        super(msg);
    }

    public CommandHandlerException(String msg,Exception obj){
        super(msg,obj);
    }
}

package cn.yang.puppet.client.exception;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class NullValueException extends Exception {
    public NullValueException(String msg){
        super(msg);
    }

    public NullValueException(String msg, Exception obj){
        super(msg,obj);
    }
}

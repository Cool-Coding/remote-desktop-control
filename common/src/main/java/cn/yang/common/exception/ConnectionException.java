package cn.yang.common.exception;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ConnectionException extends Exception {
    public ConnectionException(String msg){
        super(msg);
    }

    public ConnectionException(String msg, Exception obj){
        super(msg,obj);
    }
}

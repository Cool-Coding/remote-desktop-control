package cn.yang.common.exception;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ResponseHandleException extends Exception {
    public ResponseHandleException(String msg, Exception e){
        super(msg,e);
    }

    public ResponseHandleException(String msg){
        super(msg);
    }
}

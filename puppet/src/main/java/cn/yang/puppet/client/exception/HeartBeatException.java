package cn.yang.puppet.client.exception;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class HeartBeatException extends Exception {
    public HeartBeatException(String msg,Exception e){
        super(msg,e);
    }
}

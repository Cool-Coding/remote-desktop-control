package cn.yang.master.client.exception;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class MasterClientException extends Exception {
    public MasterClientException(String msg){
        super(msg);
    }

    public MasterClientException(String msg, Exception obj){
        super(msg,obj);
    }
}

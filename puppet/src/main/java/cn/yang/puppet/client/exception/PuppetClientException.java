package cn.yang.puppet.client.exception;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class PuppetClientException extends Exception {
    public PuppetClientException(String msg){
        super(msg);
    }

    public PuppetClientException(String msg, Exception obj){
        super(msg,obj);
    }
}

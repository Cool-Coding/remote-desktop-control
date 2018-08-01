package cn.yang.master.client.exception;

/**
 * @author cool-coding
 * @date 2018/7/26
 */
public class FireCommandHandlerException extends Exception{
    public FireCommandHandlerException(String msg){
        super(msg);
    }
    public FireCommandHandlerException(Exception e){
        super(e);
    }
}

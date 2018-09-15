package cn.yang.master.client.exception;

/**
 * @author cool-coding
 * @date 2018/7/26
 */
public class MasterChannelHandlerException extends RuntimeException{
    public MasterChannelHandlerException(String msg){
        super(msg);
    }

    public MasterChannelHandlerException(String msg,Exception e){
        super(msg,e);
    }
}

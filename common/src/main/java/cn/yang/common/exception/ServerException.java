package cn.yang.common.exception;

import java.io.Serializable;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ServerException extends Exception implements Serializable {

    private static final long serialVersionUID = -7515877337663696708L;

    public ServerException(String msg, Exception e){
        super(msg,e);
    }

    public ServerException(String msg){
        super(msg);
    }
}

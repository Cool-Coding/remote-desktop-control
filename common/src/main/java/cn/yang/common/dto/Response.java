package cn.yang.common.dto;

import cn.yang.common.command.Commands;
import cn.yang.common.exception.ServerException;

import java.io.Serializable;

/**
 * @author: cool coding
 * @date: 2018/1/7
 * 请求响应结果
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 8660758754389091659L;
    private String requestId;//请求ID
    private String puppetName;//傀儡名称
    private ServerException error;//错误
    private Enum<Commands> command;//命令
    private Object result;//结果

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public ServerException getError() {
        return error;
    }

    public void setError(ServerException error) {
        this.error = error;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Enum<Commands> getCommand() {
        return command;
    }

    public void setCommand(Enum<Commands> command) {
        this.command = command;
    }

    public Object getResult() {
        return result;
    }

    public String getPuppetName() {
        return puppetName;
    }

    public void setPuppetName(String puppetName) {
        this.puppetName = puppetName;
    }

    @Override
    public String toString() {
        return "Response{" +
                "requestId='" + requestId + '\'' +
                ", puppetName='" + puppetName + '\'' +
                ", error=" + error +
                ", command=" + command +
                ", result=" + result +
                '}';
    }
}

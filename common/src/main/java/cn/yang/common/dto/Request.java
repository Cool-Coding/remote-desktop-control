package cn.yang.common.dto;

import cn.yang.common.command.Commands;

import java.io.Serializable;

/**
 * @author Cool-Coding 2018/7/24
 * 请求封装类
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 8259360546077799485L;

    /**
     * 请求ID(控制端以M开头，傀儡端以P开头)
     */
    private String requestId;

    /**
     * /命令
     */
    private Enum<Commands> command;

    /**
     * 傀儡名
     */
    private String puppetName;

    /**
     * 请求内容
     */
    private Object value;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Enum<Commands> getCommand() {
        return command;
    }

    public void setCommand(Enum<Commands> command) {
        this.command = command;
    }

    public String getPuppetName() {
        return puppetName;
    }

    public void setPuppetName(String puppetName) {
        this.puppetName = puppetName;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", command=" + command +
                ", puppetName='" + puppetName + '\'' +
                ", value=" + value +
                '}';
    }
}

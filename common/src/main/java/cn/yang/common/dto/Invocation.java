package cn.yang.common.dto;

import cn.yang.common.command.Commands;

import java.io.Serializable;

/**
 * @author: cool coding
 * @date: 2018/1/7
 * 控制端/服务器/傀儡端 三者之间传输的对象
 */
public class Invocation implements Serializable {

    /**
     * ID(客户端标识(控制端为'M',傀儡端为'P')+MAC地址+序列号)
     */
    private String id;

    /**
     * 傀儡名
     */
    private String puppetName;


    /**
     * 命令
     */
    private Enum<Commands> command;

    /**
     * 值
     */
    private Object value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Object getValue() {
        return value;
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
                "id='" + id + '\'' +
                ", puppetName='" + puppetName + '\'' +
                ", command=" + command +
                ", value=" + value +
                '}';
    }
}

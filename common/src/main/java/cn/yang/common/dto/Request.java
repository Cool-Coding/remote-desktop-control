package cn.yang.common.dto;

/**
 * @author Cool-Coding 2018/7/24
 * 请求封装类
 */
public class Request extends Invocation {
    @Override
    public String toString() {
        return "Request{" +
                "id='" + getId() + '\'' +
                ", puppetName='" + getPuppetName() + '\'' +
                ", command=" + getCommand() +
                ", value=" + getValue() +
                '}';
    }
}

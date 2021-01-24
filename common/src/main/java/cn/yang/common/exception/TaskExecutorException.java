package cn.yang.common.exception;

/**
 * @author Cool-Coding
 * @date 2018/7/25
 */
public class TaskExecutorException extends Exception {
    public TaskExecutorException(String msg, Exception e) {
        super(msg, e);
    }
}

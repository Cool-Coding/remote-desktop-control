package cn.yang.common.exception;

public class ConfigParseException extends RuntimeException {

    public ConfigParseException(String message, Throwable cause) {
        super(message,cause);
    }
}

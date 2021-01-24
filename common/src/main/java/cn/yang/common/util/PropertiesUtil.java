package cn.yang.common.util;

import cn.yang.common.exception.ConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Cool-Coding
 * 2018/7/25
 */
public final class PropertiesUtil {
    private static final HashMap<String, Properties> propertiesMap = new HashMap<>();
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    public static int getInt(String filePath, String key, int defaultValue) {
        try {
            checkLoadConfigurationFile(filePath);
            return Integer.parseInt(getValue(filePath, key, String.valueOf(defaultValue)));
        }catch (IOException e) {
            return defaultValue;
        }
    }

    public static int getInt(String filePath, String key) {
        try {
            checkLoadConfigurationFile(filePath);
            return Integer.parseInt(getValue(filePath, key));
        } catch (Exception e) {
            throw new ConfigParseException(e.getMessage(), e.getCause());
        }
    }

    public static String getString(String filePath, String key, String defaultValue) {
        try {
            checkLoadConfigurationFile(filePath);
        } catch (IOException e) {
            return defaultValue;
        }
        return getValue(filePath, key, defaultValue);
    }

    public static String getString(String filePath, String key) {
        try {
            checkLoadConfigurationFile(filePath);
        } catch (IOException e) {
            throw new ConfigParseException(e.getMessage(), e.getCause());
        }
        return getValue(filePath, key);
    }

    private static void checkLoadConfigurationFile(String filePath) throws IOException {
        if (propertiesMap.get(filePath) == null) {
            Properties properties = new Properties();

            try {
                properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
            propertiesMap.put(filePath, properties);
        }
    }

    private static String getValue(String filePath, String key, String defaultValue) {
        return propertiesMap.get(filePath).getProperty(key, defaultValue);
    }

    private static String getValue(String filePath, String key) {
        return propertiesMap.get(filePath).getProperty(key);
    }
}

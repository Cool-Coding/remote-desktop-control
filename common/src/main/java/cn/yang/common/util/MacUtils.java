package cn.yang.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class MacUtils {

/** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MacUtils.class);

    public static String getMAC(){
        Enumeration<NetworkInterface> el;
        String mac_s = "";
        try {
            el = NetworkInterface.getNetworkInterfaces();
            while (el.hasMoreElements()) {
                byte[] mac = el.nextElement().getHardwareAddress();
                if (mac == null) {
                    continue;
                }
                mac_s = hexByte(mac[0]) + "-" + hexByte(mac[1]) + "-"
                        + hexByte(mac[2]) + "-" + hexByte(mac[3]) + "-"
                        + hexByte(mac[4]) + "-" + hexByte(mac[5]);
            }
        } catch (SocketException e1) {
            LOGGER.error(e1.getMessage(),e1);
        }
        return mac_s;

    }

    private static String hexByte(byte b) {
        String s = "000000" + Integer.toHexString(b);
        return s.substring(s.length() - 2);
    }
}

package cn.yang.puppet.client.ui;


import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.InputEvent.MouseEvent;
import cn.yang.common.constant.Constants;
import cn.yang.common.util.BeanUtil;
import cn.yang.common.util.ImageUtils;
import cn.yang.puppet.client.netty.PuppetNettyClient;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import io.netty.buffer.ByteBuf;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Key;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class PuppetDesktop {
    /**
     * 控制器
     */
    private Robot robot;
    private PuppetNettyClient puppetClient;
    private Toolkit toolkit;

    /**
     * 清晰度[0,100]
     */
    private int quality;

    public PuppetDesktop() throws AWTException{
        robot=new Robot();
        toolkit=Toolkit.getDefaultToolkit();
        //默认清晰度
        quality= Constants.SCREEN_QUALITY;
    }

    public void keyPress(MasterKeyEvent keyEvent){
        if (keyEvent.isAltDown()){
            robot.keyPress(KeyEvent.VK_ALT);
        }

        if (keyEvent.isCtrlDown()){
            robot.keyPress(KeyEvent.VK_CONTROL);
        }

        if (keyEvent.isShiftDown()){
            robot.keyPress(KeyEvent.VK_SHIFT);
        }

        robot.keyPress(keyEvent.getKeyCode());
    }


    public void keyRelease(MasterKeyEvent keyEvent){
        if (keyEvent.isAltDown()){
            robot.keyRelease(KeyEvent.VK_ALT);
        }

        if (keyEvent.isCtrlDown()){
            robot.keyRelease(KeyEvent.VK_CONTROL);
        }

        if (keyEvent.isShiftDown()){
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }

        robot.keyRelease(keyEvent.getKeyCode());
    }

    public void mouseClick(MouseEvent mouseEvent){
        final int mouseButton1 = mouseEvent.getMouseButton();
        robot.mousePress(mouseButton1);
        robot.mouseRelease(mouseButton1);
    }


    public void mouseWheel(MouseEvent mouseEvent){
        robot.mouseWheel(mouseEvent.getMouseWheel());
    }

    public void mousePress(MouseEvent mouseEvent){
        robot.mousePress(mouseEvent.getMouseButton());;
    }

    public void mouseRelease(MouseEvent mouseEvent){
        robot.mouseRelease(mouseEvent.getMouseButton());;
    }

    public void mouseMove(int[] site){
        robot.mouseMove(site[0],site[1]);
    }

    public void mouseDoubleClick(MouseEvent mouseEvent){
        final int mouseButton1 = mouseEvent.getMouseButton();
        robot.mousePress(mouseButton1);
        robot.mouseRelease(mouseButton1);
        robot.mousePress(mouseButton1);
        robot.mouseRelease(mouseButton1);
    }

    public void mouseDragged(MouseEvent mouseEvent,int[] site){
        final int mouseButton1 = mouseEvent.getMouseButton();
        robot.mousePress(mouseButton1);
        mouseMove(site);
    }

    public byte[] getScreenSnapshot(){
        //获取屏幕分辨率
        Dimension d = toolkit.getScreenSize ();
        //以屏幕的尺寸创建个矩形
        Rectangle screenRect = new Rectangle(d);
        //截图（截取整个屏幕图片）
        BufferedImage bufferedImage =  robot.createScreenCapture(screenRect);
        return ImageUtils.compressedImageAndGetByteArray(bufferedImage,quality/100.0f);
    }

    public void init(){
        puppetClient.connect();
    }

    public void setPuppetClient(PuppetNettyClient puppetClient) {
        this.puppetClient = puppetClient;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }
}

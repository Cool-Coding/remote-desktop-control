package cn.yang.puppet.client.robot;


import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.InputEvent.MasterMouseEvent;
import cn.yang.common.util.ImageUtils;
import cn.yang.puppet.client.constant.PuppetDynamicSetting;
import cn.yang.puppet.client.ui.IReplay;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class JavaRobotReplay implements IReplay {
    /**
     * 控制器
     */
    private Robot robot;
    private Toolkit toolkit;

    public JavaRobotReplay(){
        try {
            robot=new Robot();
            toolkit=Toolkit.getDefaultToolkit();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
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

    @Override
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

    @Override
    public void mouseClick(MasterMouseEvent mouseEvent){
        final int mouseButton1 = mouseEvent.getMouseButton();
        robot.mousePress(mouseButton1);
        robot.mouseRelease(mouseButton1);
    }


    @Override
    public void mouseWheel(MasterMouseEvent mouseEvent){
        robot.mouseWheel(mouseEvent.getMouseWheel());
    }

    @Override
    public void mousePress(MasterMouseEvent mouseEvent){
        System.out.println("mouse press:" + mouseEvent.getMouseButton());
        robot.mousePress(mouseEvent.getMouseButton());
    }

    @Override
    public void mouseRelease(MasterMouseEvent mouseEvent){
        System.out.println("mouse released:" + mouseEvent.getMouseButton());
        robot.mouseRelease(mouseEvent.getMouseButton());
    }

    @Override
    public void mouseMove(int[] site){
        System.out.println("mouse move:" + site[0] + ":" + site[1]);
        robot.mouseMove(site[0],site[1]);
    }

    @Override
    public void mouseDoubleClick(MasterMouseEvent mouseEvent){
        final int mouseButton1 = mouseEvent.getMouseButton();
        robot.mousePress(mouseButton1);
        robot.mouseRelease(mouseButton1);
        robot.mousePress(mouseButton1);
        robot.mouseRelease(mouseButton1);
    }

    @Override
    public void mouseDragged(MasterMouseEvent mouseEvent, int[] site){
         final int mouseButton1 = mouseEvent.getMouseButton();
        robot.mousePress(mouseButton1);
        mouseMove(site);
    }

    public Robot getRobot() {
        return robot;
    }

    @Override
    public byte[] getScreenSnapshot(){
        //获取屏幕分辨率
        Dimension d = toolkit.getScreenSize();
        //以屏幕的尺寸创建个矩形
        Rectangle screenRect = new Rectangle(d);
        //截图（截取整个屏幕图片）
        BufferedImage bufferedImage =  robot.createScreenCapture(screenRect);
        return ImageUtils.compressedImageAndGetByteArray(bufferedImage, PuppetDynamicSetting.quality/100.0f);
    }
}
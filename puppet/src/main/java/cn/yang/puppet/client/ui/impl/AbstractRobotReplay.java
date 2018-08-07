package cn.yang.puppet.client.ui.impl;


import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.InputEvent.MasterMouseEvent;
import cn.yang.puppet.client.ui.IReplay;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public abstract class AbstractRobotReplay implements IReplay {
    /**
     * 控制器
     */
    private Robot robot;

    public AbstractRobotReplay(){
        try {
            robot=new Robot();
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
        robot.mousePress(mouseEvent.getMouseButton());
    }

    @Override
    public void mouseRelease(MasterMouseEvent mouseEvent){
        robot.mouseRelease(mouseEvent.getMouseButton());
    }

    @Override
    public void mouseMove(int[] site){
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

}

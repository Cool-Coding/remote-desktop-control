package cn.yang.puppet.client.ui;

import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.InputEvent.MouseEvent;

/**
 * @author Cool-Coding
 *         2018/8/2
 */
public interface IReplay {
    /**
     * 键按下
     * @param keyEvent
     */
    void keyPress(MasterKeyEvent keyEvent);

    /**
     * 键释放
     * @param keyEvent
     */
    void keyRelease(MasterKeyEvent keyEvent);

    /**
     * 鼠标单击
     * @param mouseEvent
     */
    void mouseClick(MouseEvent mouseEvent);

    /**
     * 滚轮滚动
     * @param mouseEvent
     */
    void mouseWheel(MouseEvent mouseEvent);

    /**
     * 鼠标键按下
     * @param mouseEvent
     */
    void mousePress(MouseEvent mouseEvent);

    /**
     * 鼠标键释放
     * @param mouseEvent
     */
    void mouseRelease(MouseEvent mouseEvent);

    /**
     * 鼠标移动
     * @param site
     */
    void mouseMove(int[] site);

    /**
     * 鼠标双击
     * @param mouseEvent
     */
    void mouseDoubleClick(MouseEvent mouseEvent);

    /**
     * 鼠标拖动
     * @param mouseEvent
     * @param site
     */
    void mouseDragged(MouseEvent mouseEvent,int[] site);


    /**
     * 截屏
     * @return 图像字节数组
     */
    byte[] getScreenSnapshot();
}

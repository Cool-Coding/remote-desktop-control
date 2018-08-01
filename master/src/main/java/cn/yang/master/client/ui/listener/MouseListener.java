package cn.yang.master.client.ui.listener;

import cn.yang.common.TaskExecutors;
import cn.yang.common.command.Commands;
import cn.yang.common.util.BeanUtil;
import cn.yang.master.client.exception.MasterClientException;
import cn.yang.master.client.ui.PuppetScreen;
import cn.yang.master.client.netty.MasterNettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Cool-Coding
 *         2018/7/26
 */
public class MouseListener extends MouseAdapter {
    /**
     * 记录点击次数
     */
    private AtomicInteger clickNum=new AtomicInteger(0);

    /**
     * 是否已经
     */
    private volatile boolean isDoubleClicked;

    /**
     * 记录上次单击的鼠标键位
     */
    private int preButton=-1;

    private static final Logger LOGGER= LoggerFactory.getLogger(MouseListener.class);

    private MasterNettyClient masterClient;
    private PuppetScreen puppetScreen;

    public MouseListener(PuppetScreen puppetScreen){
        this.puppetScreen=puppetScreen;
        masterClient = BeanUtil.getBean(MasterNettyClient.class, "masterClient");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //每次单击都将此标识示false，防止双击后的单击被认为是双击
        isDoubleClicked=false;

        //点击数加1如果等于2，说明单击了两次,并且两次单击了相同的键，则是双击
        if (clickNum.incrementAndGet()==2 && isTheSameButton(e)) {
                isDoubleClicked = true;
                preButton = -1;
                this.mouseDoubleClicked(e);
                return;
        }

        //定义定时器
       TaskExecutors.submit(()->{
                if(isDoubleClicked){//如果双击事件已经执行,那么直接取消单击执行
                    clickNum.set(0);
                    isDoubleClicked=false;
                    return;
                }

            if (clickNum.getAndDecrement() == 1) {//定时器等待0.2秒后,双击事件仍未发生,执行单击事件
                    preButton = -1;
                    mouseSingleClicked(e);//执行单击事件
            }
        },2000);

        preButton=e.getButton();
    }

    //此次单击的键与上次是否为同一个键
    private boolean isTheSameButton(MouseEvent e){
        return preButton!=-1 && e.getButton()==preButton;
    }


    @Override
    public void mouseDragged(MouseEvent e) {
            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final cn.yang.common.InputEvent.MouseEvent mouseEvent = new cn.yang.common.InputEvent.MouseEvent();
            mouseEvent.dragged(e.getButton(), e.getX(), e.getY());
            LOGGER.info("mouse dragged:{}", mouseEvent);
            sendMosueEvent(mouseEvent);
    }

    @Override
    public void mousePressed(MouseEvent e) {
            final cn.yang.common.InputEvent.MouseEvent mouseEvent = new cn.yang.common.InputEvent.MouseEvent();
            mouseEvent.mousePressed(e.getButton());
            LOGGER.info("mouse pressed:{}", mouseEvent);
            sendMosueEvent(mouseEvent);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
            final cn.yang.common.InputEvent.MouseEvent mouseEvent = new cn.yang.common.InputEvent.MouseEvent();
            mouseEvent.mouseMoved(e.getX(), e.getY());
            LOGGER.info("mouse moved:{}", mouseEvent);
            sendMosueEvent(mouseEvent);
    }

    /**
     * 鼠标单击事件
     * @param e 事件源参数
     */
    public void mouseSingleClicked(MouseEvent e){
            final cn.yang.common.InputEvent.MouseEvent mouseEvent = new cn.yang.common.InputEvent.MouseEvent();
            mouseEvent.buttonClicked(e.getButton());
            mouseEvent.setSite(new int[]{e.getX(),e.getY()});
            LOGGER.info("mouse clicked", mouseEvent);
            sendMosueEvent(mouseEvent);
    }

    /**
     * 鼠标双击事件
     * @param e 事件源参数
     */
    public void mouseDoubleClicked(MouseEvent e){
            final cn.yang.common.InputEvent.MouseEvent mouseEvent = new cn.yang.common.InputEvent.MouseEvent();
            mouseEvent.buttonDoubleClick(e.getButton());
            LOGGER.info("mouse double clicked", mouseEvent);
            sendMosueEvent(mouseEvent);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
            if(e.getComponent().getCursor().getType()==Cursor.HAND_CURSOR) {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
            final cn.yang.common.InputEvent.MouseEvent mouseEvent = new cn.yang.common.InputEvent.MouseEvent();
            mouseEvent.mouseReleased(e.getButton());
            LOGGER.info("mouse released:{}", mouseEvent.getMouseButton());
            sendMosueEvent(mouseEvent);
    }

    private void sendMosueEvent(cn.yang.common.InputEvent.MouseEvent mouseEvent){
        try {
            masterClient.fireCommand(puppetScreen.getPuppetName(), Commands.MOUSE, mouseEvent);
        }catch (MasterClientException e){
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        final cn.yang.common.InputEvent.MouseEvent mouseEvent = new cn.yang.common.InputEvent.MouseEvent();
        int count=e.getScrollAmount()*e.getWheelRotation();
        mouseEvent.mouseWheel(count);
        LOGGER.info("mouse wheel:{}",count);
        sendMosueEvent(mouseEvent);
    }
}

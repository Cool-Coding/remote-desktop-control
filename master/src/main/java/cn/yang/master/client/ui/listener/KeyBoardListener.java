package cn.yang.master.client.ui.listener;

import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.command.Commands;
import cn.yang.common.util.BeanUtil;
import cn.yang.master.client.exception.MasterClientException;
import cn.yang.master.client.ui.PuppetScreen;
import cn.yang.master.client.netty.MasterNettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Cool-Coding
 *         2018/7/26
 */
public class KeyBoardListener extends KeyAdapter {

    private PuppetScreen puppetScreen;
    private MasterNettyClient masterClient;
    private static final Logger LOGGER= LoggerFactory.getLogger(KeyBoardListener.class);

    public KeyBoardListener(PuppetScreen puppetScreen){
        this.puppetScreen=puppetScreen;
        masterClient = BeanUtil.getBean(MasterNettyClient.class, "masterClient");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        LOGGER.info(KeyEvent.getKeyText(e.getKeyCode())+" pressed");
        fireCommand(e,true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
            LOGGER.info(KeyEvent.getKeyText(e.getKeyCode())+" released");
            fireCommand(e,false);
    }

    public void fireCommand(KeyEvent e,boolean pressed){
        final MasterKeyEvent keyEvent = new MasterKeyEvent();
        keyEvent.setPressed(pressed);
        if (e.isAltDown()){
            keyEvent.setAltDown(true);
        }

        if (e.isControlDown()){
            keyEvent.setCtrlDown(true);
        }

        if (e.isShiftDown()){
            keyEvent.setShiftDown(true);
        }
        keyEvent.setKeyCode(e.getKeyCode());
        try {
            masterClient.fireCommand(puppetScreen.getPuppetName(), Commands.KEYBOARD, keyEvent);
        }catch (MasterClientException e2){
            JOptionPane.showMessageDialog(null,e2.getMessage());
        }
    }

    public void setMasterClient(MasterNettyClient masterClient) {
        this.masterClient = masterClient;
    }
}

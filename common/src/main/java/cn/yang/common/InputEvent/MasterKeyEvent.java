package cn.yang.common.InputEvent;

import java.awt.event.KeyEvent;

/**
 * @author Cool-Coding
 *         2018/7/26
 */
public class MasterKeyEvent {

    /**
     * 按键状态
     * TRUE.按下
     * FALSE.释放
     */
    private boolean pressed;

    private boolean altDown;
    private boolean ctrlDown;
    private boolean shiftDown;

    private int keyCode;

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isAltDown() {
        return altDown;
    }

    public void setAltDown(boolean altDown) {
        this.altDown = altDown;
    }

    public boolean isCtrlDown() {
        return ctrlDown;
    }

    public void setCtrlDown(boolean ctrlDown) {
        this.ctrlDown = ctrlDown;
    }

    public boolean isShiftDown() {
        return shiftDown;
    }

    public void setShiftDown(boolean shiftDown) {
        this.shiftDown = shiftDown;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    @Override
    public String toString() {
        return "MasterKeyEvent{" +
                "pressed=" + pressed +
                ", altDown=" + altDown +
                ", ctrlDown=" + ctrlDown +
                ", shiftDown=" + shiftDown +
                ", keyCode=" + keyCode +
                '}';
    }
}

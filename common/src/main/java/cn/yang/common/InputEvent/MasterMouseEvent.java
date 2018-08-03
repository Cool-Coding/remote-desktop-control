package cn.yang.common.InputEvent;

import java.util.Arrays;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON2_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;

/**
 * @author Cool-Coding
 *         2018/7/26
 */
public class MasterMouseEvent {
    /**
     * 1.单击
     * 2.双击
     * 3.拖拽
     * 4.移动
     * 5.滚轮滚动
     * 6.按下
     * 7.释放
     */
    private byte status;

    /**
     * 鼠标按键
     */
    private MouseButton mouseButton;

    /**
     * 滚轮滚动的大小
     */
    private int mouseWheel;

    /**
     * (x,y)坐标
     */
    private int[] site ;

    public MasterMouseEvent(){
        site=new int[2];
    }

    public void buttonClicked(int mouseButton){
        this.status=1;
        this.mouseButton= ConvertToMouseButton(mouseButton);
    }



    public void buttonDoubleClick(int mouseButton){
        this.status=2;
        this.mouseButton= ConvertToMouseButton(mouseButton);
    }


    public void dragged(int mouseButton,int x,int y){
        this.status=3;
        this.mouseButton= ConvertToMouseButton(mouseButton);
        setSite(x,y);
    }


    public void mouseMoved(int x,int y){
        this.status=4;
        setSite(x,y);
    }


    public void mouseWheel(int mouseWheel){
        this.status=5;
        this.mouseWheel=mouseWheel;
    }

    public void mousePressed(int mouseButton){
        this.status=6;
        this.mouseButton= ConvertToMouseButton(mouseButton);
    }

    public void mouseReleased(int mouseButton){
        this.status=7;
        this.mouseButton= ConvertToMouseButton(mouseButton);
    }

    private void setSite(int x,int y){
        site[0]=x;
        site[1]=y;
    }

    public boolean isClicked(){
        return this.status==1;
    }

    public boolean isDoubleClicked(){
        return this.status==2;
    }

    public boolean isDragged(){
        return this.status==3;
    }

    public boolean isMouseMoved(){
        return this.status==4;
    }

    public boolean isMouseWheel(){
        return this.status==5;
    }

    public boolean isMousePressed(){return this.status==6;};
    public boolean isMouseReleased(){return this.status==7;};

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public int getMouseButton() {
        if (mouseButton==null){
            return 0;
        }

        switch (mouseButton){
            case LEFT:
                return BUTTON1_DOWN_MASK;
            case MIDDLE:
                return  BUTTON2_DOWN_MASK;
            case RIGHT:
                return BUTTON3_DOWN_MASK;
            case PRESSED:
                return java.awt.event.MouseEvent.MOUSE_PRESSED;
            case DRAGGED:
                return BUTTON1_DOWN_MASK;
            case RELEASED:
                return java.awt.event.MouseEvent.MOUSE_RELEASED;
            case WHEEL:
                return java.awt.event.MouseEvent.MOUSE_WHEEL;
            case CLICK:
                return java.awt.event.MouseEvent.MOUSE_CLICKED;
            default:
                throw new RuntimeException("wrong MouseButton value:"+mouseButton);
        }
    }

    public int getMouseWheel() {
        return mouseWheel;
    }

    public void setMouseWheel(int mouseWheel) {
        this.mouseWheel = mouseWheel;
    }

    public int[] getSite() {
        return site;
    }

    public void setSite(int[] site) {
        this.site = site;
    }

    private MouseButton ConvertToMouseButton(int mouseButton){
        switch (mouseButton){
            case java.awt.event.MouseEvent.BUTTON1:
                return MouseButton.LEFT;
            case java.awt.event.MouseEvent.BUTTON2:
                return MouseButton.MIDDLE;
            case java.awt.event.MouseEvent.BUTTON3:
                return MouseButton.RIGHT;
            case 0://调试发现为0时，表示拖动，鼠标移动也为零，但不调用此方法
                return MouseButton.DRAGGED;
            case java.awt.event.MouseEvent.MOUSE_WHEEL:
                return MouseButton.WHEEL;
            case java.awt.event.MouseEvent.MOUSE_CLICKED:
                return MouseButton.CLICK;
            case java.awt.event.MouseEvent.MOUSE_PRESSED:
                return MouseButton.PRESSED;
            case java.awt.event.MouseEvent.MOUSE_RELEASED:
                return MouseButton.RELEASED;
            default:
                throw new RuntimeException("not supported mouse button");
        }
    }


    @Override
    public String toString() {
        return "MasterMouseEvent{" +
                "status=" + status +
                ", mouseButton=" + mouseButton +
                ", mouseWheel=" + mouseWheel +
                ", site=" + Arrays.toString(site) +
                '}';
    }
}

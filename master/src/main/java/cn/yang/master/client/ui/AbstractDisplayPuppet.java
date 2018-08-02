package cn.yang.master.client.ui;

import cn.yang.common.util.BeanUtil;
import cn.yang.master.client.ui.listener.KeyBoardListener;
import cn.yang.master.client.ui.listener.MouseListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Cool-Coding
 *         2018/8/2
 */
public abstract class AbstractDisplayPuppet implements IDisplayPuppet {
    protected final IMasterDesktop masterDesktop = BeanUtil.getBean(IMasterDesktop.class);

    /**
     * 窗体
     */
    private final JFrame jFrame;

    /**
     * 傀儡名
     */
    protected final String puppetName;

    /**
     * 傀儡屏幕
     */
    private CanvasPanel imageJpanel;

    AbstractDisplayPuppet(String puppetName){
        this.puppetName=puppetName;
        this.jFrame=new JFrame();

        setting();
        initBody();
        initMenu(jFrame);
    }

    /**
     * 窗体属性设置
     */
    public void setting(){
        jFrame.setLocation(250, 250);
        jFrame.setSize(500,500);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                masterDesktop.terminate(puppetName);
            }
        });
    }

    /**
     * 初使化主体
     */
    public void initBody(){
        imageJpanel=new CanvasPanel();
        jFrame.add(imageJpanel);
        final KeyBoardListener keyBoardListener = new KeyBoardListener(AbstractDisplayPuppet.this);
        final MouseListener mouseListener = new MouseListener(AbstractDisplayPuppet.this);
        jFrame.addKeyListener(keyBoardListener);
        imageJpanel.addMouseListener(mouseListener);
        imageJpanel.addMouseMotionListener(mouseListener);
        imageJpanel.addMouseWheelListener(mouseListener);
    }

    @Override
    public void launch() {
        SwingUtilities.invokeLater(()->{
            jFrame.setVisible(true);
        });
    }

    @Override
    public void refresh(byte[] bytes) {
        generateImage(bytes);
        SwingUtilities.invokeLater(() -> {
            this.imageJpanel.repaint();
        });
    }

    /**
     * 远程桌面
     */
    private class CanvasPanel extends JPanel {
        private static final long serialVersionUID = -3313907120784874523L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            AbstractDisplayPuppet.this.paintComponent(g);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            AbstractDisplayPuppet.this.paint(g);
        }
    }

    /**
     * 由子类具体的画图逻辑
     * @param g
     */
    protected void paint(Graphics g){};

    protected void paintComponent(Graphics g){};
    /**
     * 窗口引用，用于子类进行菜单功能扩展
     * @return
     */
    public JFrame getjFrame() {
        return jFrame;
    }


    /**
     * 处理字节数组,生成bufferedImage或像素,由子类决定
     * @param bytes
     */
    abstract void generateImage(byte[] bytes);

    /**
     * 初始化菜单，由子类实现
     * @param jFrame
     */
    abstract void initMenu(JFrame jFrame);
    @Override
    public String getPuppetName() {
        return puppetName;
    }
}

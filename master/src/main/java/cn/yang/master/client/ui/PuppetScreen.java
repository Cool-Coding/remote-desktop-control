package cn.yang.master.client.ui;

import cn.yang.common.TaskExecutors;
import cn.yang.common.command.Commands;
import cn.yang.common.constant.Constants;
import cn.yang.common.util.ImageUtils;
import cn.yang.master.client.exception.MasterClientException;
import cn.yang.master.client.ui.listener.KeyBoardListener;
import cn.yang.master.client.ui.listener.MouseListener;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.StringUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * @author cool-coding
 * 2018/7/27
 */
public class PuppetScreen implements ActionListener{

    private CanvasPanel imageJpanel;
    private MasterDesktop masterDesktop;
    private String puppetName;
    private QualitySlider qualitySlider;
    private JFrame jFrame;
    private BufferedImage image;

    PuppetScreen(String puppetName,MasterDesktop masterDesktop){
        this.masterDesktop=masterDesktop;
        this.puppetName=puppetName;

        jFrame=new JFrame();

        imageJpanel=new CanvasPanel();
        jFrame.add(imageJpanel);

        JMenuBar menuBar=new JMenuBar();
        JMenu setting=new JMenu("设置");
        menuBar.add(setting);
        jFrame.setJMenuBar(menuBar);

        JMenuItem qualityItem=new JMenuItem("清晰度");
        qualityItem.setActionCommand(Commands.QUALITY.name());
        qualityItem.addActionListener(this);
        setting.add(qualityItem);

        jFrame.setLocation(250, 250);
        jFrame.setSize(500,500);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                masterDesktop.terminate(puppetName);
            }
        });

        final KeyBoardListener keyBoardListener = new KeyBoardListener(PuppetScreen.this);
        final MouseListener mouseListener = new MouseListener(PuppetScreen.this);
        jFrame.addKeyListener(keyBoardListener);
        imageJpanel.addMouseListener(mouseListener);
        imageJpanel.addMouseMotionListener(mouseListener);
        imageJpanel.addMouseWheelListener(mouseListener);
    }

    void lanuch(){
        SwingUtilities.invokeLater(()->{
            jFrame.setVisible(true);
        });
    }

    void refresh(BufferedImage image){
        this.image= image;

        SwingUtilities.invokeLater(() -> {
            this.imageJpanel.repaint();
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (Commands.valueOf(e.getActionCommand())){
            case QUALITY:
                changeQuality();
                break;
            default:
        }
    }

    public void changeQuality(){
        if (qualitySlider==null){
            qualitySlider=new QualitySlider();
        }

        SwingUtilities.invokeLater(()->{
            qualitySlider.setVisible(true);
        });
    }


    //滑动条
    private class QualitySlider extends JDialog{
        private static final long serialVersionUID = 5807019525801501790L;
        private int quality=Constants.SCREEN_QUALITY;
        private JSlider slider;
        private final static String OK_COMMAND_BUTTON="OK";

        public QualitySlider(){
            super(PuppetScreen.this.getjFrame());
            Box sliderBox= new Box(BoxLayout.Y_AXIS);

            slider = new JSlider(10,100,quality);
            //设置绘制刻度
            slider.setPaintTicks(true);
            //设置主、次刻度的间距
            slider.setMajorTickSpacing(20);
            slider.setMinorTickSpacing(5);
            //设置绘制刻度标签，默认绘制数值刻度标签
            slider.setPaintLabels(true);
            slider.addChangeListener((event)->{
                //取出滑动条的值，并向傀儡端发送命令
                JSlider source = (JSlider) event.getSource();
                quality=source.getValue();
            });

            JPanel buttonJpanel=new JPanel();
            JButton button=new JButton("确定");
            button.setActionCommand(OK_COMMAND_BUTTON);
            buttonJpanel.add(button);
            button.addActionListener((e)->{
                    if (OK_COMMAND_BUTTON.equals(e.getActionCommand())){
                        TaskExecutors.submit(()->{
                            try {
                                masterDesktop.getMasterClient().fireCommand(puppetName, Commands.QUALITY, quality);
                            }catch (MasterClientException e2){
                                SwingUtilities.invokeLater(()->{
                                    JOptionPane.showMessageDialog(PuppetScreen.this.getjFrame(),e2.getMessage());
                                });
                            }
                        },0);

                    }
                    QualitySlider.this.setVisible(false);});

            sliderBox.add(slider);
            sliderBox.add(buttonJpanel);

            add(sliderBox);
            pack();
            setTitle("清晰度调节");
            setModalityType(ModalityType.APPLICATION_MODAL);
            this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        }
    }


    //远程桌面
    class CanvasPanel extends JPanel {

        private static final long serialVersionUID = -3313907120784874523L;

        @Override
        public  void paint(Graphics g) {
            super.paint(g);
            try {
                if(image!=null) {
                    g.drawImage(image, 0, 0, null);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public String getPuppetName() {
        return puppetName;
    }

    public JFrame getjFrame() {
        return jFrame;
    }
}

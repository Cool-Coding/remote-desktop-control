package cn.yang.master.client.ui;

import cn.yang.common.TaskExecutors;
import cn.yang.common.command.Commands;
import cn.yang.common.constant.Constants;
import cn.yang.common.util.BeanUtil;
import cn.yang.common.util.ImageUtils;
import cn.yang.master.client.exception.MasterClientException;
import cn.yang.master.client.ui.listener.KeyBoardListener;
import cn.yang.master.client.ui.listener.MouseListener;
import org.springframework.beans.BeanUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * @author cool-coding
 * 2018/7/27
 */
public class PuppetScreen extends AbstractDisplayPuppet implements ActionListener{

    private QualitySlider qualitySlider;
    private BufferedImage image;


    public PuppetScreen(String puppetName){
        super(puppetName);
    }


    @Override
    public void initMenu(JFrame jFrame) {
        JMenuBar menuBar=new JMenuBar();
        JMenu setting=new JMenu("设置");
        menuBar.add(setting);
        jFrame.setJMenuBar(menuBar);

        JMenuItem qualityItem=new JMenuItem("清晰度");
        qualityItem.setActionCommand(Commands.QUALITY.name());
        qualityItem.addActionListener(this);
        setting.add(qualityItem);
    }

    /**
     * 改变清晰度
     */
    private void changeQuality(){
        if (qualitySlider==null){
            qualitySlider=new QualitySlider();
        }

        SwingUtilities.invokeLater(()->{
            qualitySlider.setVisible(true);
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

    @Override
    protected void paint(Graphics g) {
        if(image!=null){
            g.drawImage(image,0,0,null);
        }
    }

    @Override
    void generateImage(byte[] bytes) {
        this.image= ImageUtils.getImageFromByteArray(bytes);
    }

    //滑动条
    private class QualitySlider extends JDialog{
        private static final long serialVersionUID = 5807019525801501790L;
        private int quality=Constants.SCREEN_QUALITY;
        private JSlider slider;
        private final static String OK_COMMAND_BUTTON="OK";

        private QualitySlider(){
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
                                masterDesktop.fireCommand(puppetName, Commands.QUALITY, quality);
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
}

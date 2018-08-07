package cn.yang.master.client.ui;

import cn.yang.common.TaskExecutors;
import cn.yang.common.command.Commands;
import cn.yang.common.util.BeanUtil;
import cn.yang.master.client.constant.ExceptionMessageConstants;
import cn.yang.master.client.exception.MasterClientException;
import cn.yang.master.client.netty.MasterNettyClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Cool-Coding
 *         2018/7/26
 */
public class MasterDesktop extends JFrame implements IMasterDesktop,ActionListener{
    private MasterNettyClient masterClient;
    private HashMap<String,IDisplayPuppet> puppets=new HashMap<>();

    private JTextField puppetNameTextField;

    public MasterDesktop(){
      setting();
      initMenu();
      initBody();
    }

    @Override
    public void lanuch(){
        try {
            SwingUtilities.invokeAndWait(() -> {
                setVisible(true);
            });
            connect();
        }catch (Exception e){
            popToShowMessage(ExceptionMessageConstants.LAUNCH_FAILED,e.getMessage());
        }
    }

    @Override
    public void setting(){
        setSize(400,300);
        setResizable(false);
        setLocation(250, 250);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                TaskExecutors.shutdown();
                for(String puppetName:puppets.keySet()){
                    terminate0(puppetName);
                }
            }
        });
    }

    @Override
    public void initMenu(){
        JMenuBar menuBar=new JMenuBar();
        setJMenuBar(menuBar);

        JMenu jMenu=new JMenu("操作");
        menuBar.add(jMenu);

        JMenuItem connect=new JMenuItem("连接");
        connect.setActionCommand(Commands.CONNECT.name());
        connect.addActionListener(this);
        jMenu.add(connect);

        JMenuItem control=new JMenuItem("远程");
        control.setActionCommand(Commands.CONTROL.name());
        control.addActionListener(this);
        jMenu.add(control);
    }

    @Override
    public void initBody(){
        Font titleFont=new Font("宋体",Font.CENTER_BASELINE,25);
        Font contentFont=new Font("宋体",Font.PLAIN,20);

        JPanel jPanel=new JPanel(new GridLayout(3,1));

        JPanel titlePanel=new JPanel();
        JTextArea jTitle=new JTextArea(3,10);
        jTitle.setText("远程桌面控制");
        jTitle.setFont(titleFont);
        jTitle.setOpaque(false);
        jTitle.setEditable(false);
        titlePanel.add(jTitle);
        jPanel.add(titlePanel);

        JPanel jPanel5=new JPanel();
        jPanel.add(jPanel5);
        JLabel jLabel=new JLabel();
        jLabel.setText("被控端名称:");
        jLabel.setFont(contentFont);
        jPanel5.add(jLabel);

        puppetNameTextField =new JTextField(15);
        puppetNameTextField.setToolTipText("输入被控端名称");
        puppetNameTextField.setFont(contentFont);
        jPanel5.add(puppetNameTextField);

        JPanel jPanel6=new JPanel();
        jPanel.add(jPanel6);
        JButton jButton=new JButton();
        jButton.setText("远程");
        jButton.setActionCommand(Commands.CONTROL.name());
        jButton.addActionListener(this);
        jPanel6.add(jButton);

        final Container contentPane = getContentPane();
        contentPane.add(BorderLayout.CENTER,jPanel);
    }

    /**
     * 启动傀儡桌面的窗口
     * @param puppetName
     */
    @Override
    public void lanuch(String puppetName){
        final IDisplayPuppet puppetScreen = BeanUtil.getBean(IDisplayPuppet.class,puppetName);
        puppets.put(puppetName,puppetScreen);
        puppetScreen.launch();
    }

    @Override
    public void refreshScreen(String puppetName,byte[] bytes) {
        //如果当前正处理控制状态，则显示傀儡发过来的屏幕截图，否则忽略
        // (当向傀儡发送终止命令后，在傀儡收到命令前，仍会发送屏幕截图)
        final IDisplayPuppet puppetScreen = puppets.get(puppetName);
        if (puppetScreen != null) {
            puppetScreen.refresh(bytes);
        }
    }

    @Override
    public void connect() throws Exception{
        masterClient.connect();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       switch (Commands.valueOf(e.getActionCommand())){
           case CONNECT:
               try {
                   connect();
               }catch (Exception e2){
                   popToShowMessage(ExceptionMessageConstants.CONNECTION_SERVER_FAILED,e2.getMessage());
               }
               break;
           case CONTROL:
               if (StringUtils.isEmpty(puppetNameTextField.getText())){
                   popToShowMessage(ExceptionMessageConstants.PUPPET_NAME_EMPTY);
               }else {
                   String puppetName=puppetNameTextField.getText();
                   try {
                       masterClient.fireCommand(puppetName, Commands.CONTROL, null);
                   }catch (MasterClientException e2){
                        popToShowMessage(e2.getMessage());
                   }
               }
            break;
            default:
       }
    }

    @Override
    public void terminate(String puppetName){
        terminate0(puppetName);
        puppets.remove(puppetName);
    }

    private void terminate0(String puppetName){
        try {
            masterClient.fireCommand(puppetName, Commands.TERMINATE, null);
        }catch (MasterClientException e){
            popToShowMessage(e.getMessage());
        }
    }

    @Override
    public void fireCommand(String puppetName, Enum<Commands> command, Object data) throws MasterClientException {
        masterClient.fireCommand(puppetName,command,data);
    }

    public void setMasterClient(MasterNettyClient client) {
        this.masterClient = client;
    }

    public void showMessage(String message){
        JOptionPane.showMessageDialog(this,message);
    }

    private void popToShowMessage(String... message){
        JOptionPane.showMessageDialog(this, Arrays.toString(message));
    }
}
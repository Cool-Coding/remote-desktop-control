package cn.yang.puppet.client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Cool-Coding
 *         2018/8/3
 */
public class MessageDialog extends JDialog {
    private JTextField message;

    public MessageDialog(String title) {
        setSize(400, 200);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2, screenSize.height / 2);
        message = new JTextField();
        message.setFont(new Font("宋体", Font.PLAIN, 20));
        setTitle(title);
        add(message);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            this.message.setText(message);
            MessageDialog.this.setVisible(true);
        });
    }
}

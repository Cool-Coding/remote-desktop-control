package cn.yang.puppet.client.ui.impl;


import cn.yang.common.constant.Constants;
import cn.yang.common.util.ImageUtils;
import cn.yang.common.netty.INettyClient;
import cn.yang.puppet.client.netty.PuppetNettyClient;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class PuppetDesktop extends AbstractRobotReplay {

    private INettyClient puppetClient;
    private Toolkit toolkit;

    /**
     * 清晰度[10,100]
     */
    private int quality;

    public PuppetDesktop(){
        toolkit=Toolkit.getDefaultToolkit();
        //默认清晰度
        quality= Constants.SCREEN_QUALITY;
    }

    @Override
    public byte[] getScreenSnapshot(){
        //获取屏幕分辨率
        Dimension d = toolkit.getScreenSize ();
        //以屏幕的尺寸创建个矩形
        Rectangle screenRect = new Rectangle(d);
        //截图（截取整个屏幕图片）
        BufferedImage bufferedImage =  getRobot().createScreenCapture(screenRect);
        return ImageUtils.compressedImageAndGetByteArray(bufferedImage,quality/100.0f);
    }

    @Override
    public void connect() throws Exception{

        puppetClient.connect();
    }

    public void setPuppetClient(PuppetNettyClient puppetClient) {
        this.puppetClient = puppetClient;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }
}

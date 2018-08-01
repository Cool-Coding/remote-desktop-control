package cn.yang.server.netty;

import io.netty.channel.Channel;

/**
 * @author Cool-Coding
 * @date 2018/7/24
 */
public class ChannelPair {
    /**
     * 控制端通道
     */
    Channel masterChannel;

    /**
     * 傀儡端通道
     */
    Channel puppetChannel;

    public Channel getMasterChannel() {
        return masterChannel;
    }

    public void setMasterChannel(Channel masterChannel) {
        this.masterChannel = masterChannel;
    }

    public Channel getPuppetChannel() {
        return puppetChannel;
    }

    public void setPuppetChannel(Channel puppetChannel) {
        this.puppetChannel = puppetChannel;
    }
}

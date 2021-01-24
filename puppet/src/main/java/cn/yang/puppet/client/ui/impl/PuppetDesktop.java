package cn.yang.puppet.client.ui.impl;


import cn.yang.common.netty.INettyClient;
import cn.yang.puppet.client.netty.PuppetNettyClient;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class PuppetDesktop implements INettyClient {

    private INettyClient puppetClient;


    @Override
    public void connect() throws Exception{
        puppetClient.connect();
    }

    public void setPuppetClient(PuppetNettyClient puppetClient) {
        this.puppetClient = puppetClient;
    }

}

package cn.yang.puppet.client.netty;

import cn.yang.common.ChannelInitializerNew;
import cn.yang.common.dto.Request;
import cn.yang.common.TaskExecutors;
import cn.yang.common.command.Commands;
import cn.yang.common.constant.Constants;
import cn.yang.common.sequence.SequenceGenerator;
import cn.yang.common.util.ExtensionLoader;
import cn.yang.puppet.client.constant.ConfigConstants;
import cn.yang.puppet.client.constant.ExceptionMessageConstants;
import cn.yang.common.util.PropertiesUtil;
import cn.yang.puppet.client.exception.PuppetClientException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Cool-Coding
 * 2018/7/24
 */
public class PuppetNettyClient {
    /**
     * 处理器初始化器
     */
    private ChannelInitializerNew channelInitialize;

    private NioEventLoopGroup group;

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(PuppetNettyClient.class);

    private String host;
    private int port;

    public void init() throws PuppetClientException{
        group = new NioEventLoopGroup();
        try {
            host = PropertiesUtil.getString(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SERVER_IP);
            port = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SERVER_PORT);
        }catch (IOException e){
            throw new PuppetClientException(e.getMessage(),e);
        }
    }

    public void connect(){
        try {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(channelInitialize);
            final ChannelFuture sync = bootstrap.connect(host, port).sync();
            if (channelInitialize.getChannelHandler() instanceof PuppetNettyClientHandler) {
                //启动一个独立线程，以免阻塞spring实例化bean
                TaskExecutors.submit(()->{
                    try {
                        sync.channel().writeAndFlush(buildRequest());
                    }catch (Exception e){
                        LOGGER.error(e.getMessage(),e);
                    }
                },0);
            } else {
                throw new RuntimeException(ExceptionMessageConstants.PUPPET_HANDLER_ERROR);
            }
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }finally {
            //如果连接断开了，重新与服务器连接
            LOGGER.info("与服务器{}:{}连接断开，重新连接.",host,port);
            try {
                TaskExecutors.submit(()->{
                    connect();
                },PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.RECONNECT_INTERVAL));
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    public void setChannelInitialize(ChannelInitializerNew channelInitialize) {
        this.channelInitialize = channelInitialize;
    }

    private Request buildRequest() throws PuppetClientException{
        try {
            final SequenceGenerator generator = ExtensionLoader.getSequenceGenerator();
            final Request request = new Request();
            request.setId(""+Constants.PUPPET + generator.next() );
            request.setCommand(Commands.CONNECT);
            request.setPuppetName("puppet");
            return request;
        }catch (IOException e){
           throw new PuppetClientException(e.getMessage(),e);
        }
    }

    public void destroy(){
        group.shutdownGracefully();
    }

}

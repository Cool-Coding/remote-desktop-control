package cn.yang.master.client.netty;

import cn.yang.common.ChannelInitializerNew;
import cn.yang.common.dto.Request;
import cn.yang.common.TaskExecutors;
import cn.yang.common.command.Commands;
import cn.yang.common.constant.Constants;
import cn.yang.common.sequence.SequenceGenerator;
import cn.yang.common.util.SequenceGeneratorUtil;
import cn.yang.common.util.MacUtils;
import cn.yang.master.client.constant.ExceptionConstants;
import cn.yang.common.util.PropertiesUtil;
import cn.yang.master.client.constant.ConfigConstants;

import cn.yang.master.client.exception.MasterChannelHandlerException;
import cn.yang.master.client.exception.MasterClientException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Cool-Coding
 * @date 2018/7/24
 */
public class MasterNettyClient {
    /**
     * 处理器初始化器
     */
    private ChannelInitializerNew channelInitialize;

    private NioEventLoopGroup group;

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterNettyClient.class);

    private String host;
    private int port;

    public void init(){
        group = new NioEventLoopGroup();
        try {
            host = PropertiesUtil.getString(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SERVER_IP);
            port = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SERVER_PORT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void connect() throws Exception{
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(channelInitialize);
        final ChannelFuture sync = bootstrap.connect(host, port).sync();
        sync.channel().writeAndFlush(buildConnectRequest());

        //启动一个独立线程，以免阻塞spring实例化bean
        TaskExecutors.submit(()->{
            try {
                sync.channel().closeFuture().sync();
            }catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
        },0);
    }

    private MasterNettyClientHandler getChannelHandler(){
        if (channelInitialize.getChannelHandler() instanceof MasterNettyClientHandler) {
            return  (MasterNettyClientHandler) channelInitialize.getChannelHandler();
        } else {
            final String message = String.format("%s %s",ExceptionConstants.HANDLER_NOT_SUPPORTED, channelInitialize.getChannelHandler());
            LOGGER.error(message);
            throw new RuntimeException(message);
        }
    }

    public void fireCommand(String puppetName,Enum<Commands> command,Object data) throws MasterClientException{
        try {
            getChannelHandler().sendCommand(puppetName,command,data);
        }catch (MasterChannelHandlerException e){
           throw new MasterClientException(e.getMessage(),e);
        }
    }

    public void setChannelInitialize(ChannelInitializerNew channelInitialize) {
        this.channelInitialize = channelInitialize;
    }

    public Request buildConnectRequest() throws MasterClientException{
        String mac= MacUtils.getMAC();
        if (StringUtils.isEmpty(mac)){
            return null;
        }

        try {
            final SequenceGenerator generator = SequenceGeneratorUtil.getSequenceGenerator();

            Request request = new Request();
            request.setRequestId(Constants.MASTER + mac + generator.next());
            request.setCommand(Commands.CONNECT);
            return request;
        }catch (IOException e){
          throw  new MasterClientException(e.getMessage(),e);
        }
    }

    public void destroy(){
        group.shutdownGracefully();
    }
}

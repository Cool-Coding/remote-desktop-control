package cn.yang.puppet.client.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.Commands;
import cn.yang.common.util.PropertiesUtil;
import cn.yang.puppet.client.constant.ConfigConstants;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.puppet.client.exception.HeartBeatException;
import io.netty.channel.ChannelHandlerContext;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ConnectCommandHandler extends AbstractPuppetCommandHandler {

    private String host;
    private int port;

    public ConnectCommandHandler() throws CommandHandlerException{
        try {
            host = PropertiesUtil.getString(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SERVER_IP);
            port = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SERVER_PORT);
        }catch (IOException e){
            throw new CommandHandlerException(e.getMessage(),e);
        }
    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        sendHeartBeat(ctx);
    }

    public void sendHeartBeat(ChannelHandlerContext ctx) throws HeartBeatException {
        try {
            //发送心跳
            int interval = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.HEARTBEAT_INTERVAL);
            ctx.executor().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Request heartBeatRequest = null;
                    if(isUnderControlled()){
                        final byte[] bytes = puppetDesktop.getScreenSnapshot();
                        heartBeatRequest = buildRequest(Commands.SCREEN, bytes);
                    }else {
                        heartBeatRequest=getHeartBeatRequest();
                        LOGGER.debug("send a heartbeat to {}:{}", host, port);
                    }

                    if (heartBeatRequest!=null) {
                        if (ctx.channel()!=null && ctx.channel().isOpen()) {
                            ctx.writeAndFlush(heartBeatRequest);
                        }
                    }
                }
            }, 0, interval, TimeUnit.MILLISECONDS);
        }catch (IOException e){
            ctx.executor().shutdownGracefully();
            throw new HeartBeatException(e.getMessage(),e);
        }
    }

    public Request getHeartBeatRequest(){
        return buildRequest(Commands.HEARTBEAT,null);
    }
}

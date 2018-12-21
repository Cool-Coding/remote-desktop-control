package cn.yang.puppet.client.netty;

import cn.yang.common.util.CommandHandlerLoader;
import cn.yang.common.command.handler.ICommandHandler;
import cn.yang.common.dto.Response;
import cn.yang.common.util.PropertiesUtil;
import cn.yang.puppet.client.commandhandler.AbstractPuppetCommandHandler;
import cn.yang.puppet.client.constant.ConfigConstants;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
@ChannelHandler.Sharable
public class PuppetNettyClientHandler extends SimpleChannelInboundHandler<Response> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(PuppetNettyClientHandler.class);
    private static final Map<String,Integer> ERROR_COUNT_HASHMAP=new ConcurrentHashMap<>();
    private static final int ERROR_COUNT=PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH,ConfigConstants.ERROR_COUNT,50);

    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        LOGGER.debug(response.toString());
        if (response.getError()!=null){
            String message=response.getError().getMessage();
            if(!StringUtils.isEmpty(message)) {
                LOGGER.error(message);
                if (!ERROR_COUNT_HASHMAP.containsKey(message)){
                    ERROR_COUNT_HASHMAP.put(message,1);
                    return;
                }

                //判断是大于限制的次数,关闭与服务器之间的连接(比如控制端停止了控制，而傀儡没有收到命令，则会一直发送屏幕截图)
                //超过错误次数，关闭连接，重新与服务器连接，在连接时，服务器进行判断是否需要停止发送屏幕截图
            if(ERROR_COUNT_HASHMAP.get(message) >= ERROR_COUNT){
                    ctx.close();
                    return;
                }

                //记数加1
                ERROR_COUNT_HASHMAP.put(message,ERROR_COUNT_HASHMAP.get(message)+1);
            }
            return;
        }

        final ICommandHandler commandHandler = CommandHandlerLoader.getCommandHandler(response.getCommand());
        if(commandHandler!=null) {
            commandHandler.handle(ctx, response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(),cause);
        ctx.close();
    }
}

package cn.yang.common.command.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public interface ICommandHandler<T> {
    /**
     *
     * @param ctx           当前channel处理器上下文
     * @param inbound       channel输入对象
     * @throws Exception    异常
     */
    void handle(ChannelHandlerContext ctx,T inbound) throws Exception;
}

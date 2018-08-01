package cn.yang.common.command.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public interface ICommandHandler<T> {
    void handle(ChannelHandlerContext ctx,T inbound) throws Exception;
}

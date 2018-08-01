package cn.yang.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Cool-Coding
 *         2018/7/25
 * 使用单例，由Spring管理
 */
@Deprecated
public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {

    /**
     * 编码器
     */
    private ChannelHandler encoder;

    /**
     * 解码器
     */
    private ChannelHandler decoder;

    /**
     * 处理器
     */
    private ChannelHandler channelHandler;

    /**
     * 每新建一个连接，都添加一遍encode/decoder/channelHandler，会报错:不是@Sharable
     * 但是如果在每个类上添加@Shareable注解，由于encoder、decoder继承ByteToMessageDecoder，
     * 而ByteToMessageDecoder的子类不允许有@Sharable注解，所以encoder和decoder采用新建对象的方式
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    .addLast(encoder) // 将请求进行编码（为了发送请求）
                    .addLast(decoder)// 将 RPC 响应进行解码（为了处理响应）
                    .addLast(channelHandler); // 使用 HeartBeatNettyClient 发送心跳
    }

    public void setEncoder(ChannelHandler encoder) {
        this.encoder = encoder;
    }

    public void setDecoder(ChannelHandler decoder) {
        this.decoder = decoder;
    }


    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }
}

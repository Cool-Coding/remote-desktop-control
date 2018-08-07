package cn.yang.common.netty;

import cn.yang.common.serialization.ProtobufDecoder;
import cn.yang.common.serialization.ProtobufEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Cool-Coding
 *         2018/7/25
 * 使用单例，由Spring管理
 */
public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {

    /**
     * 处理器
     */
    private ChannelHandler channelHandler;

    private Class<?> requestClass;
    private Class<?> responseClass;

    public ChannelInitializer(Class<?> requestClass, Class<?> responseClass){
        this.requestClass=requestClass;
        this.responseClass=responseClass;
    }

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
                    .addLast(new ProtobufDecoder(requestClass))
                    .addLast(new ProtobufEncoder(responseClass))
                    .addLast(channelHandler);
    }

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }
}

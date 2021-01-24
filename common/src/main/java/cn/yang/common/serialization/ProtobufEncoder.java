package cn.yang.common.serialization;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User:cool coding
 * Date:2018/1/7
 * Time:21:33
 * 编码
 */
public class ProtobufEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public ProtobufEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    private static final Logger LOGGER= LoggerFactory.getLogger(ProtobufEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if(genericClass.isInstance(in)){
            byte[] data= SerializationUtil.serialize(in);
            int length=data.length;
            out.writeInt(length);
            out.writeBytes(data);
            if (genericClass== Request.class) {
                LOGGER.debug("send data to server,size:{}", length);
            }else if(genericClass== Response.class){
                LOGGER.debug("send data to client,size:{}",length);
            }

        }
    }

    @Override
    public boolean isSharable() {
        return super.isSharable();
    }
}

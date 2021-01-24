package cn.yang.common.serialization;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * User:cool coding
 * Date:2018/1/7
 * Time:21:27
 * 解码
 */
public class ProtobufDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public ProtobufDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    private static final Logger LOGGER= LoggerFactory.getLogger(ProtobufDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<4){
             return;
        }

        in.markReaderIndex();

        int dataLength=in.readInt();
        if(dataLength<0) ctx.close();

        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }

        if (genericClass== Request.class) {
            LOGGER.debug("receive data from client,size:{}", dataLength);
        }else if(genericClass== Response.class){
            LOGGER.debug("receive data from server,size:{}",dataLength);
        }

        byte[] data=new byte[dataLength];
        in.readBytes(data);

        Object obj= SerializationUtil.deSerialize(data,genericClass);
        out.add(obj);

    }

    @Override
    public boolean isSharable() {
        return super.isSharable();
    }
}

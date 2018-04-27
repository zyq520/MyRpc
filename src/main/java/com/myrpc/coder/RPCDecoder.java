package com.myrpc.coder;

import com.myrpc.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;


/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:30
 **/
public class RPCDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RPCDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final int length = in.readableBytes();
        final byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        Object obj = SerializationUtil.deserialize(bytes, genericClass);
        out.add(obj);
    }
}

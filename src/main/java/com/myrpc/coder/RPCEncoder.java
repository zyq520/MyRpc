package com.myrpc.coder;

import com.myrpc.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:30
 **/
public class RPCEncoder extends MessageToByteEncoder<Object> {
    private Class<?> genericClass;
    public RPCEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = SerializationUtil.serialize(in);
            out.writeBytes(data);
        }
    }
}

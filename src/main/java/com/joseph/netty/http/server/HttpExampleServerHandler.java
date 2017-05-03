package com.joseph.netty.http.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by dongxinyu on 2017/5/2.
 */
public class HttpExampleServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //判断请求类型，只处理POST请求
        if(request.method() != HttpMethod.POST){
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        //获取请求内容
        ByteBuf buf = request.content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String context = new String(bytes, "UTF-8");
        System.out.println(context);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("content-type", "text/html;charset=UTF-9");
        StringBuilder sb = new StringBuilder();
        sb.append("test is ok");
        ByteBuf buffer = Unpooled.copiedBuffer(sb, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, status);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}

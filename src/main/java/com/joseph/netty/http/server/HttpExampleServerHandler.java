package com.joseph.netty.http.server;

import com.google.common.util.concurrent.RateLimiter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


/**
 * Created by dongxinyu on 2017/5/2.
 */
public class HttpExampleServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Logger logger = LoggerFactory.getLogger(HttpExampleServerHandler.class);
    private final static RateLimiter limiter = RateLimiter.create(2.0);//限制每秒2个请求

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        limiter.acquire();

        //判断请求类型，只处理POST请求
        if(request.method() != HttpMethod.POST){
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        //获取，将uri当作topic名称处理，测试例子topic名字为test，做uri合法性验证
        String topic = parseUri(request.uri());
        if(!Objects.equals("test", topic)){
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        //路径验证通过后获取请求内容作为kafka消息发送
        ByteBuf buf = request.content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String context = new String(bytes, "UTF-8");

        try {
            KafkaClient.send(topic, context);
        }catch (Exception e){
            logger.error("发送Kafka消息失败！", e);
            sendError(ctx, HttpResponseStatus.BAD_GATEWAY);
            return;
        }

        //发送成功后响应请求
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("content-type", "text/html;charset=UTF-8");
        StringBuilder sb = new StringBuilder();
        sb.append("ok");
        ByteBuf buffer = Unpooled.copiedBuffer(sb, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, status);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private String parseUri(String uri){
        return uri.substring(1, uri.length());
    }
}

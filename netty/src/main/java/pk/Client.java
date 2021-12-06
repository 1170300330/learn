package pk;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.group(eventLoopGroup);
            bootstrap.remoteAddress("127.0.0.1", 8000);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new NettyClientHandler());
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8000).sync();
            if (channelFuture.isSuccess()) {
                System.err.println("连接服务器成功");
            }
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}

class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf firstMessage;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] data = "你好，服务器".getBytes();
        firstMessage = Unpooled.buffer();
        firstMessage.writeBytes(data);
        ctx.writeAndFlush(firstMessage);
        System.err.println("客户端发送消息:你好，服务器");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        String rev = getMessage(buf);
        System.err.println("客户端收到服务器消息:" + rev);
    }

    private String getMessage(ByteBuf buf) {
        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);
        return new String(con, StandardCharsets.UTF_8);
    }
}

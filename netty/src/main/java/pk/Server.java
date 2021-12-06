package pk;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024); // 连接数
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); // 长连接
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel)
                        throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();
                    p.addLast(new NettyServerHandler());// 添加NettyServerHandler，用来处理Server端接收和处理消息的逻辑
                }
            });
            ChannelFuture channelFuture = bootstrap.bind(8000).sync();
            if (channelFuture.isSuccess()) {
                System.err.println("启动Netty服务成功，端口号：" + 8000);
            }
            // 关闭连接
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            System.err.println("启动Netty服务异常，异常信息：" + e.getMessage());
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf buf = (ByteBuf) msg;

        String recieved = getMessage(buf);
        System.err.println("服务器接收到客户端消息：" + recieved);

        try {
            ctx.writeAndFlush(getSendByteBuf("你好，客户端"));
            System.err.println("服务器回复消息：你好，客户端");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /*
     * 从ByteBuf中获取信息 使用UTF-8编码返回
     */
    private String getMessage(ByteBuf buf) {

        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);
        return new String(con, StandardCharsets.UTF_8);
    }

    private ByteBuf getSendByteBuf(String message)
            throws UnsupportedEncodingException {

        byte[] req = message.getBytes(StandardCharsets.UTF_8);
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);

        return pingMessage;
    }
}

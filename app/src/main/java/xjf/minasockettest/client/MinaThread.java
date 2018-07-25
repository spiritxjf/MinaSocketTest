package xjf.minasockettest.client;

import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class MinaThread extends Thread {

    private IoSession session = null;

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Log.d("TEST","客户端链接开始...");
        IoConnector connector = new NioSocketConnector();
        //设置链接超时时间
        connector.setConnectTimeoutMillis(30000);
        //添加过滤器
        //connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CharsetCodecFactory()));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));
        connector.setHandler(new MinaClientHandler());

        try{
            ConnectFuture future = connector.connect(new InetSocketAddress(ConstantUtil.WEB_MATCH_PATH,ConstantUtil.WEB_MATCH_PORT));//创建链接
            future.awaitUninterruptibly();// 等待连接创建完成
            session = future.getSession();//获得session
            session.write("start");
        }catch (Exception e){
            Log.d("TEST","客户端链接异常...");
        }
        session.getCloseFuture().awaitUninterruptibly();//等待连接断开
        Log.d("TEST","客户端断开...");
        connector.dispose();
        super.run();
    }

}

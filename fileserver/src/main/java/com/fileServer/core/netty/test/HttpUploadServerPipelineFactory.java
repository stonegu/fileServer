package com.fileServer.core.netty.test;

import static org.jboss.netty.channel.Channels.*;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;

public class HttpUploadServerPipelineFactory implements ChannelPipelineFactory {
    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        if (HttpUploadServer.isSSL) {
                SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
                engine.setUseClientMode(false);
                SslHandler handler = new SslHandler(engine);
                handler.setIssueHandshake(true);
                pipeline.addLast("ssl", handler);
        }

        pipeline.addLast("decoder", new HttpRequestDecoder());

        pipeline.addLast("encoder", new HttpResponseEncoder());

        // Remove the following line if you don't want automatic content compression.
        pipeline.addLast("deflater", new HttpContentCompressor());

        pipeline.addLast("handler", new HttpUploadServerHandler());
        return pipeline;
    }
}

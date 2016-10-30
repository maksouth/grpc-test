package com.kambius.protobuff;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloClient {
    private static final Logger logger = Logger.getLogger(HelloClient.class.getName());

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public HelloClient(String host, int port) throws SSLException {
        channel = NettyChannelBuilder.forAddress(host, port)
            .sslContext(GrpcSslContexts
                .forClient()
                .keyManager(
                    new File(getClass().getClassLoader().getResource("client.crt").getFile()),
                    new File(getClass().getClassLoader().getResource("client.new.key").getFile()))
                .trustManager(new File(getClass().getClassLoader().getResource("server.crt").getFile()))
                .build())
            .build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void greet(String text) {
        logger.info("Sending: " + text);

        HelloRequest request = HelloRequest.newBuilder().setText(text).build();

        HelloReply response;

        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }

        logger.info("Response: " + response.getText());
    }

    public static void main(String[] args) throws Exception {
        HelloClient client = new HelloClient("localhost", 8080);
        try {
            client.greet("hello");
        } finally {
            client.shutdown();
        }
    }
}
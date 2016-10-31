package com.test.protobuf;

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

    private static final int PORT = 8080;
    private static final String HOST = "localhost";

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public HelloClient(String host, int port) throws SSLException {
        // configure ssl context to secure connection
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
    public void greet(String name) {
        logger.info("Sending: " + name);

        HelloRequest request = HelloRequest.newBuilder().setName(name).build();

        HelloReply response;

        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }

        logger.info("Response: " + response.getGreeting());
    }

    public static void main(String[] args) throws Exception {
        HelloClient client = new HelloClient(HOST, PORT);

        try {
            String name = "world";
            if (args.length > 0) name = args[0]; // Use the arg as the name to greet if provided
            client.greet(name);
        } finally {
            client.shutdown();
        }
    }
}
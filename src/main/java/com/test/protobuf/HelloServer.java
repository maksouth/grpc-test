package com.test.protobuf;

import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Sample java server to test gRPC
 */
public class HelloServer {
    private static final Logger logger = Logger.getLogger(HelloServer.class.getName());

    private static final int PORT = 8080;
    private Server server;

    private void start() throws IOException {

        // configure ssl context to secure connection
        SslContext sslContext = GrpcSslContexts.forServer(
            new File(getClass().getClassLoader().getResource("server.crt").getFile()),
            new File(getClass().getClassLoader().getResource("server.new.key").getFile())
        ).trustManager(
            new File(getClass().getClassLoader().getResource("ca.crt").getFile())
        ).clientAuth(ClientAuth.REQUIRE).build();


        server = NettyServerBuilder
            .forPort(PORT)
            .sslContext(sslContext)
            .addService(new GreeterImpl())
            .build()
            .start();

        logger.info("HelloServer started, listening on " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.warning("Shutting down gRPC server since JVM is shutting down");
                HelloServer.this.stop();
                logger.warning("Server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the gRPC library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        HelloServer server = new HelloServer();
        server.start();
        server.blockUntilShutdown();
    }

    private class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            logger.info("Request from " + req.getName());
            HelloReply reply = HelloReply.newBuilder().setGreeting("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}

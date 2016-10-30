package com.kambius.protobuff;

import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
<<<<<<< HEAD
=======
import io.netty.handler.ssl.ClientAuth;
>>>>>>> fd5b2bd4a70a6de9537b2182b486acadfcb9dfab
import io.netty.handler.ssl.SslContext;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Sample java server to test gRPC
 */
public class HelloServer {
    private static final Logger logger = Logger.getLogger(HelloServer.class.getName());

    private int port = 8080;
    private Server server;

    private void start() throws IOException {
<<<<<<< HEAD
//        server = ServerBuilder
//            .forPort(port)
//            .addService(new GreeterImpl())
//            .build()
//            .start();
        SslContext sslContext = GrpcSslContexts.forServer(
            new File(getClass().getClassLoader().getResource("server.crt").getFile()),
            new File(getClass().getClassLoader().getResource("server.key").getFile())
        ).build();
=======

        SslContext sslContext = GrpcSslContexts.forServer(
            new File(getClass().getClassLoader().getResource("server.crt").getFile()),
            new File(getClass().getClassLoader().getResource("server.new.key").getFile())
        ).trustManager(
            new File(getClass().getClassLoader().getResource("ca.crt").getFile())
        ).clientAuth(ClientAuth.REQUIRE).build();
>>>>>>> fd5b2bd4a70a6de9537b2182b486acadfcb9dfab


        server = NettyServerBuilder
            .forPort(port)
<<<<<<< HEAD
//            .useTransportSecurity(
//                new File(getClass().getClassLoader().getResource("server.crt").getFile()),
//                new File(getClass().getClassLoader().getResource("server.key").getFile())
//            )
=======
>>>>>>> fd5b2bd4a70a6de9537b2182b486acadfcb9dfab
            .sslContext(sslContext)
            .addService(new GreeterImpl())
            .build()
            .start();

        logger.info("HelloServer started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("Shutting down gRPC server since JVM is shutting down");
                HelloServer.this.stop();
                System.err.println("Server shut down");
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
            HelloReply reply;

            if (req.getText().equals("hello")) {
                reply = HelloReply.newBuilder().setText("world").build();
            } else {
                reply = HelloReply.newBuilder().setText("unknown request").build();
            }

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}

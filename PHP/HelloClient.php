#!/bin/php
<?php

require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/message.php';

<<<<<<< HEAD
$client = new sample\GreeterClient('localhost:8080', [
    'credentials' => Grpc\ChannelCredentials::createInsecure(),
=======
$credentials = Grpc\ChannelCredentials::createSsl(
    file_get_contents(__DIR__ . '/certificates/server.crt'),
    file_get_contents(__DIR__ . '/certificates/client.new.key'),
    file_get_contents(__DIR__ . '/certificates/client.crt')
);

$client = new sample\GreeterClient('localhost:8080', [
    'credentials' => $credentials,
>>>>>>> fd5b2bd4a70a6de9537b2182b486acadfcb9dfab
]);

$request = new sample\HelloRequest();
$request->setText('hello');

list($reply, $status) = $client->SayHello($request)->wait();

echo $reply->getText() . "\n";

?>
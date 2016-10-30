#!/bin/php
<?php

require dirname(__FILE__) . '/vendor/autoload.php';

$client = new helloworld\GreeterClient('localhost:8080', [
    'credentials' => Grpc\ChannelCredentials::createInsecure(),
]);

$request = new helloworld\HelloRequest();
$request->setText('hello');

list($reply, $status) = $client->SayHello($request)->wait();

echo $reply->getText() . "\n";


?>
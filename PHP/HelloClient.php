#!/bin/php
<?php

require_once __DIR__ . "/vendor/autoload.php";
require_once __DIR__ . "/greeter.php";

$host = "localhost";
$port = 8080;

// configure ssl context to secure connection
$credentials = Grpc\ChannelCredentials::createSsl(
    file_get_contents(__DIR__ . "/certificates/server.crt"),
    file_get_contents(__DIR__ . "/certificates/client.new.key"),
    file_get_contents(__DIR__ . "/certificates/client.crt")
);

echo "Connecting to server\n";
$client = new test\GreeterClient($host . ":" . $port, ["credentials" => $credentials]);

$name = !empty($argv[1]) ? $argv[1] : 'world';
echo "Sending: " . $name . "\n";

$request = new test\HelloRequest();
$request->setName($name);

// send request to server
list($reply, $status) = $client->SayHello($request)->wait();

echo "Received: " . $reply->getGreeting() . "\n";

?>
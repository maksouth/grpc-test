#!/bin/php
<?php

require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/Protos/HelloMessage.php';

$socket = fsockopen("localhost", 8080);    // connect to server
$stream = Protobuf\Stream::wrap($socket);  // wrap server stream with protobuf

$sw = new Protobuf\Binary\StreamWriter(Protobuf\Configuration::getInstance());
$sr = new Protobuf\Binary\StreamReader(Protobuf\Configuration::getInstance());

$hello = new  Protos\HelloMessage(); // create hello message
$hello->setText("hello");            //

$size = $hello->serializedSize(                  // get message size
    Protobuf\Configuration::getInstance()->      
    createComputeSizeContext());                 
$sw->writeVarint($stream, $size);                // write size to stream
$stream->writeStream($hello->toStream(), $size); // write message to stream

$size = $sr->readVarint($stream);                                         // read response size  
$response = \Protos\HelloMessage::fromStream($stream->readStream($size)); // read response

echo $response->getText() . "\n";

?>
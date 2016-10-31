# Sample gRPS project
## Java - PHP communication

### Install dependencies:
```
# binary packages
sudo apt-get install php7.0 php7.0-dev zlib1g-dev
sudo pecl install grpc
sudo apt-get install openssl

# composer
curl -sS https://getcomposer.org/installer | php
sudo mv composer.phar /usr/local/bin/composer

# protobuf compiller
git clone https://github.com/stanley-cheung/Protobuf-PHP
cd Protobuf-PHP
# don't forget to add pear directory to php include dir
# (add /usr/share/pear to /etc/php/php.ini)
rake pear:package version=1.0
sudo pear install Protobuf-1.0.tgz
cd ..
rm -rf Protobuf-PHP
```

### Generate ssl certificates(optional):
```
# test sertificates are allready generated(password: test)
cd certificates
./generate # you can customize sertificate details in this file
cp * ../PHP/certificates/
cp * ../src/main/resources/
```

### Build server:
```
cd PBuffTest
./gradlew jar
```

### Build client:
```
cd PHP
composer install
./generate_protos # or just protoc-gen-php -i . -o . ./greeter.proto
```

### Run server
```
java -jar build/libs/protobuff-0.0.1-SNAPSHOT.jar
```

### Run client
```
cd PHP
php -d extension=grpc.so HelloClient.php YourName
```
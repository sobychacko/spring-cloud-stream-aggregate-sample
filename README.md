# spring-cloud-stream-aggregate-sample

This repo is a collection of following individual maven modules.

* Netty TCP Source
* Payload Deserializer
* Spring Cloud Stream Aggregate Application that combines the above 2 modules to form a protocol server
* A demo app to send data to the Netty TCP source

### Netty TCP Source

Read more about the Netty TCP souce in the netty project.

Netty is a good choice for writing protocol servers. 
The Spring Cloud Stream Netty TCP source supports both binary and textual data using the various decoders provided by Netty out of the box. 

### Payload Deserializer

This is a Spring Cloud Stream Processor using the Spring Integration's PayloadDeserializingTransformer. 
It receives data through an incoming channel, process the data and put the deserialized data into the output channel.
One important thing about this deseriallizer is that the end users are expected to provide a deserializer that implements Spring's Deserializer. Otherwise, by default it provides a no-op echo deserializer that produces the same byte array passed to it. 

### Aggregate Application

The aggregate application combines the apps produced from the above 2 components into a single application. 
In the payload-deserializer app, it customizes to provide two deserializers each one to be activated based on the following properties. 

1. payload.deserializer.text
2. payload.deserializer.binary

one for deserializing textual data and other for binary.

The binary deserializer deserializes a contrived protocol that has the following structure. 

Length field (4 bytes) | count for text fields | text field 1 length | text field 1 | ....| text field n length | text field n | Numeric field (4 bytes)

## Steps to run the Aggregate application

1. Clone this repo
2. cd netty
3. ./mvnw clean install (-PgenerateApps to produce the standalone middleware based apps)
4. cd ../payload-deserializer
5. ./mvnw clean install (-PgenerateApps to produce the standalone middleware based apps)
6. cd ../netty-ingester-aggregate-kafka-10
7. ./mvnw clean package

8. java -jar target/netty-ingester-aggregate-kafka-10-1.0.0.BUILD-SNAPSHOT.jar --spring.cloud.stream.bindings.output.destination=fox --netty.tcp.port=29001 --netty.tcp.decoder=LENGTH --netty.tcp.lengthFieldOffset=0 --netty.tcp.lengthFieldLength=4 --netty.tcp.maxFrameLength=1024 --payload.deserializer.binary=true

### Quick explanation of the properties passed:

spring.cloud.stream.bindings.output.destination=fox - Bind the output from the source to a destination named `fox` on the broker. 

netty.tcp.port=29001 - The port that Netty TCP server will listen for incoming data

netty.tcp.decoder=LENGTH - Netty decoder to user

netty.tcp.lengthFieldOffset=0 - Where doesn length field begins in the frame

netty.tcp.lengthFieldLength=4 - Length of the length field

netty.tcp.maxFrameLength=1024 - Maximum length of the incoming TCP frame that Netty consumes before passing it downstream

payload.deserializer.binary=true - Tell the aggregate application to choose the binary deserializer


Now, run a standalone log sink application as below:

 java -jar target/log-sink-kafka-10-1.2.0.BUILD-SNAPSHOT.jar --spring.cloud.stream.bindings.input.destination=fox

### Push data to the Netty TCP server

cd push-data-tcp

This is a small spring boot app that sends data to the Netty TCP server. 

./mvnw clean package

java -jar target/push-data-tcp-1.0.0.BUILD-SNAPSHOT.jar 100

This sends 100 binary messages to the Netty TCP server based on the fictional binary protocol above. 

You should see all those messages sent in the log sink console. 

### Running Line based data to the Netty TCP Server

java -jar target/netty-ingester-aggregate-kafka-10-1.0.0.BUILD-SNAPSHOT.jar --netty.tcp.port=29001 --netty.tcp.decoder=LINE --netty.tcp.maxLineLength=256 --spring.cloud.stream.bindings.output.destination=fox --payload.deserializer.text=true

On another terminal do the following:

echo "hello netty tcp source" | nc localhost 29001

You should see this message coming through the log sink console





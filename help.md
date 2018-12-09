 Message
The org.springframework.integration.Message interface defines the spring Message: the unit of data transfer within a Spring Integration context.

public interface Message<T> {
    T getPayload();
    MessageHeaders getHeaders();
}
It defines accessors to two key elements:
Message headers, essentially a key-value container that can be used to transmit metadata, as defined in the org.springframework.integration.MessageHeaders class
The message payload, which is the actual data that is of value to be transferred — in our use-case, the video file is the payload
.2. Channel
A channel in Spring Integration (and indeed, EAI) is the basic plumbing in an integration architecture. It’s the pipe by which messages are relayed from one system to another.
You can think of it as a literal pipe through which an integrated system or process can push messages to (or receive messages from) other systems.
Channels in Spring Integration come in various flavors, depending on your need. They are largely configurable and usable out of the box, without any custom code, but should you have custom needs, there’s a robust framework available.
Point-to-Point (P2P) channels are used to establish 1-to-1 communication lines between systems or components. One component publishes a message to the channel so another can pick it up. There can be only one component at each end of the channel.
As we have seen, configuring a channel is as simple as returning an instance of DirectChannel:
1
2
3
4
5
6
7
8
9
10
11
12
13
14
@Bean
public MessageChannel fileChannel1() {
    return new DirectChannel();
}
 
@Bean
public MessageChannel fileChannel2() {
    return new DirectChannel();
}
 
@Bean
public MessageChannel fileChannel3() {
    return new DirectChannel();
}
Here, we have defined three separate channels all identified by the name of their respective getter methods.
Publish-Subscribe (Pub-Sub) channels are used to establish a one-to-many communication line between systems or components. This will allow us to publish to all 3 of the direct channels that we created earlier.
So following our example, we can replace the P2P channel with a pub-sub channel:
1
2
3
4
5
6
7
8
9
10
11
12
13
@Bean
public MessageChannel pubSubFileChannel() {
    return new PublishSubscribeChannel();
}
 
@Bean
@InboundChannelAdapter(value = "pubSubFileChannel", poller = @Poller(fixedDelay = "1000"))
public MessageSource<File> fileReadingMessageSource() {
    FileReadingMessageSource sourceReader = new FileReadingMessageSource();
    sourceReader.setDirectory(new File(INPUT_DIR));
    sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
    return sourceReader;
}
We have now converted the inbound channel adapter to publish to a Pub-Sub channel. This will allow us to send the files that are being read from the source folder to multiple destinations.
5.3. Bridge
A bridge in Spring Integration is used to connect two message channels or adapters if for any reason they can’t connect directly.
In our case, we can use a bridge to connect our Pub-Sub channel to three different P2P channels (because P2P and Pub-Sub channels can’t be connected directly):
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
@Bean
@BridgeFrom(value = "pubSubFileChannel")
public MessageChannel fileChannel1() {
    return new DirectChannel();
}
 
@Bean
@BridgeFrom(value = "pubSubFileChannel")
public MessageChannel fileChannel2() {
    return new DirectChannel();
}
 
@Bean
@BridgeFrom(value = "pubSubFileChannel")
public MessageChannel fileChannel3() {
    return new DirectChannel();
}
The above bean configuration now bridges the pubSubFileChannel to three P2P channels. The @BridgeFrom annotation is what defines a bridge and can be applied to any number of channels that need to subscribe to the Pub-Sub channel.
We can read the above code as “create a bridge from the pubSubFileChannel to fileChannel1, fileChannel2, and fileChannel3 so that messages from pubSubFileChannel can be fed to all three channels simultaneously.”
5.4. Service Activator
The Service Activator is any POJO that defines the @ServiceActivator annotation on a given method. This allows us to execute any method on our POJO when a message is received from an inbound channel, and it allows us to write messages to an outward channel.
In our example, our service activator receives a file from the configured input channel and writes it to the configured folder.
5.5. Adapter
The Adapter is an enterprise integration pattern-based component that allows one to “plug-in” to a system or data source. It is almost literally an adapter as we know it from plugging into a wall socket or electronic device.
It allows reusable connectivity to otherwise “black-box” systems like databases, FTP servers and messaging systems such as JMS, AMQP, and social networks like Twitter. The ubiquity of the need to connect to these systems means that adapters are very portable and reusable (in fact there’s a small catalog of adapters, freely available and ready to use by anyone).
Adapters fall into two broad categories — inbound and outbound.
Let’s examine these categories in the context of the adapters in use in our sample scenario:
Inbound adapters, as we have seen, are used to bring in messages from the external system (in this case a filesystem directory).
Our inbound adapter configuration consists of:
An @InboundChannelAdapter annotation that marks the bean configuration as an adapter — we configure the channel to which the adapter will feed its messages (in our case, an MPEG file) and a poller, a component which helps the adapter poll the configured folder at the specified interval
A standard Spring java configuration class that returns a FileReadingMessageSource, the Spring Integration class implementation that handles filesystem polling
Outbound adapters are used to send messages outwards. Spring Integration supports a large variety of out-of-the-box adapters for various common use cases.



		https://docs.spring.io/spring-integration/reference/html/endpoint-summary.html


	https://docs.spring.io/spring-integration/reference/html/
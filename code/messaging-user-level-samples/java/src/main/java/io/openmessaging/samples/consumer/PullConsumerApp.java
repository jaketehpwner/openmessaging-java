package io.openmessaging.samples.consumer;

import io.openmessaging.Message;
import io.openmessaging.MessageHeaderBuiltinKeys;
import io.openmessaging.MessagingAccessPoint;
import io.openmessaging.MessagingAccessPointFactory;
import io.openmessaging.OMS;
import io.openmessaging.PullConsumer;
import io.openmessaging.ResourceManager;

public class PullConsumerApp {
    public static void main(String[] args) {
        final MessagingAccessPoint messagingAccessPoint = MessagingAccessPointFactory
            .getMessagingAccessPoint("openmessaging:rocketmq://localhost:10911/namespace");
        messagingAccessPoint.startup();
        System.out.println("MessagingAccessPoint startup OK");
        ResourceManager resourceManager = messagingAccessPoint.getResourceManager();

        resourceManager.createAndUpdateQueue("HELLO_QUEUE", OMS.newKeyValue());
        //PullConsumer only can pull messages from one queue.
        final PullConsumer pullConsumer = messagingAccessPoint.createPullConsumer("HELLO_QUEUE");

        pullConsumer.startup();

        //Poll one message from queue.
        Message message = pullConsumer.poll();

        //Acknowledges the consumed message
        pullConsumer.ack(message.sysHeaders().getString(MessageHeaderBuiltinKeys.MESSAGE_ID));

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                pullConsumer.shutdown();
                messagingAccessPoint.shutdown();
            }
        }));
    }
}

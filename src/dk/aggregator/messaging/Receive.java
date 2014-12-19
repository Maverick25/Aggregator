/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.aggregator.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.util.HashMap;


/**
 *
 * @author marekrigan
 */
public class Receive 
{
    private static final String TASK_QUEUE_NAME = "queue_aggregator";
    private static Connection connection;
    private static Channel channel;
    
    public static HashMap<String,Object> setUpReceiver() throws java.io.IOException
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("datdb.cphbusiness.dk");
        factory.setUsername("student");
        factory.setPassword("cph");
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(TASK_QUEUE_NAME, false, consumer);

        HashMap<String,Object> returnObjects = new HashMap<>();
        
        returnObjects.put("channel",channel);
        returnObjects.put("consumer",consumer);
        
        return returnObjects;
    }
    
    public static void closeConnections() throws IOException
    {
        channel.close();
        connection.close();
    }
   
}

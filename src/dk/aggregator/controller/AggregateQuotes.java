/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.aggregator.controller;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import dk.aggregator.dto.LoanResponseDTO;
import dk.aggregator.messaging.Receive;
import dk.aggregator.messaging.Send;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author marekrigan
 */
public class AggregateQuotes 
{
    private static Gson gson;
    
    public static void receiveMessages() throws IOException,InterruptedException
    {
        gson = new Gson();
        
        HashMap<String,Object> objects = Receive.setUpReceiver();
        List<LoanResponseDTO> dtos = null;
        
        QueueingConsumer consumer = (QueueingConsumer) objects.get("consumer");
        Channel channel = (Channel) objects.get("channel");
        
        LoanResponseDTO loanResponseDTO;
        
        while (true) 
        {
          QueueingConsumer.Delivery delivery = consumer.nextDelivery();
          String message = new String(delivery.getBody());
          AMQP.BasicProperties props = delivery.getProperties();
          AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(props.getCorrelationId()).build();
          
          loanResponseDTO = gson.fromJson(message, LoanResponseDTO.class);
          
          System.out.println(loanResponseDTO.toString());
          
          sendMessage(loanResponseDTO, replyProps);

          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
        
    }
    
    public static void sendMessage(LoanResponseDTO dto, AMQP.BasicProperties props) throws IOException
    {
        String message = gson.toJson(dto);
        
        Send.sendMessage(message,props);
    }
}

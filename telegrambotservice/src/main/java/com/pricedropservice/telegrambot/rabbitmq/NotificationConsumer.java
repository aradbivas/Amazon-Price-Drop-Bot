package com.pricedropservice.telegrambot.rabbitmq;

import clients.product.ProductResponse;
import com.pricedropservice.telegrambot.AmazonPriceDropBot;
import com.pricedropservice.telegrambot.BotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final AmazonPriceDropBot BotService;
    @RabbitListener(queues ="${rabbitmq.queue.notification}")
    public void consumer(ProductResponse productResponse)
    {
        log.info("consumed {} from queue", productResponse);
        BotService.sendNotification(productResponse);
    }
}

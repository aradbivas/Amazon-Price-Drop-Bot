package com.pricedropservice.telegrambot;

import clients.product.ProductRequest;
import clients.product.ProductResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@AllArgsConstructor
@RequestMapping("api/v1/bot")

public class BotController {
    AmazonPriceDropBot amazonPriceDropBot;
    @PostMapping("/notify")
    public void sendNotification(@RequestBody ProductResponse productResponse)
    {
        amazonPriceDropBot.sendNotification(productResponse);
    }
}

package com.pricedropservice.telegrambot;

import clients.product.ProductRequest;
import clients.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Slf4j

@Component
public class AmazonPriceDropBot extends TelegramLongPollingBot
{
    private final BotService botService;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage())
        {

            handleMessage(update.getMessage());

        }
        if(update.hasCallbackQuery())
        {
            handleCallback(update.getCallbackQuery());
        }

    }

    @SneakyThrows
    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String data = callbackQuery.getData();
        String[] param = data.split(":");
        String action = param[0];

        switch (action)
        {
            case "product":
            {
                ProductResponse product = botService.getProduct(param[1]);
                List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                buttons.add(
                        Arrays.asList(
                                InlineKeyboardButton.builder()
                                        .text(product.getTitle().toString() +" " + "price: "  + product.getCurrentPrice().toString() +"$   " + "   ❌")
                                        .callbackData("delete:" + product.getId())
                                        .build()));
                execute(SendMessage.builder()
                        .text("press on item to delete it")
                        .chatId(message.getChatId().toString())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                        .build());
                return;
            }
            case "delete":
            {
               botService.deleteProduct(param[1]);
                execute(SendMessage.builder()
                        .text("Item deleted")
                        .chatId(message.getChatId().toString())
                        .build());
                return;
            }
            default:
        }

    }
    @SneakyThrows
    public void sendNotification(ProductResponse productResponse)
    {
        execute(SendMessage.builder().text("Price drop on " + productResponse.getTitle() +", click on the url to get it!!").chatId(productResponse.getUserId()).build());
        execute(SendMessage.builder().text(productResponse.getUrl()).chatId(productResponse.getUserId()).build());
    }
    @SneakyThrows
    public void handleMessage(Message message)
    {
        if(message.hasText() && message.hasEntities())
        {

            if(message.getText().contains("https://www.amazon.com/"))
            {
                try
                {

                    ProductResponse response = addItem(message,message.getText());
                        execute(SendMessage.builder()
                                .text("Product "+ response.getTitle() +" " + response.getCurrentPrice() + "$ added successfully")
                                .chatId(message.getChatId().toString())
                                .build());
                }
                catch (Exception ex)
                {
                    execute(SendMessage.builder()
                            .text(ex.getMessage().toString())
                            .chatId(message.getChatId().toString())
                            .build());
                }

            }
            Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if(commandEntity.isPresent())
            {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                String[] data = command.split(" ");

                switch (command)
                {
                    case "/add_item": {

                        return;
                    }
                    case "/my_items": {
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        List<ProductResponse> response = botService.getProducts(message.getFrom().getId().toString());
                        for(ProductResponse product : response)
                        {
                            buttons.add(
                                    Arrays.asList(
                                            InlineKeyboardButton.builder()
                                                    .text(product.getTitle().toString())
                                                    .callbackData("product:" + product.getId())
                                                    .build()));
                        }
                        execute(SendMessage.builder()
                                .text("Press on Item to get more information")
                                .chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build());
                        return;

                    }
                }
            }
        }
    }
    public ProductResponse addItem(Message message, String data)
    {
        User user = message.getFrom();
        Long userId =user.getId();
        String userFirstName = user.getFirstName();
        Long userChatId = message.getChatId();
        ProductResponse productResponse = botService.addItem(data, userId, userFirstName, userChatId.toString());
        return productResponse;
    }
    @Override
    public String getBotUsername() {
        return "AmazonPriceDropBot";
    }

    @Override
    public String getBotToken() {
        return "5940861453:AAFzgqrEmMfRQWox5Jv8nUXJxKmI98KbaSs";
    }
}

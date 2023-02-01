package com.pricedropservice.telegrambot;

import clients.Exception.ApiRequestException;
import clients.product.ProductResponse;
import clients.product.ProductClient;
import clients.product.ProductRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BotService {


    private final ProductClient productClient;

    //TODO:Change return type to Product
    public ProductResponse addItem(String url, Long userId, String userFirstName, String userChatId) {
        ResponseEntity<ProductResponse> responseEntity;

        try
        {
            ProductRequest productRequest = new ProductRequest(userId, userFirstName, url, userChatId);
            responseEntity = productClient.addProduct(productRequest);
            if(responseEntity.getStatusCode() == HttpStatus.OK)
            {
            }
        }
        catch (Exception ex)
        {
            throw new ApiRequestException("No products found");

        }
        return responseEntity.getBody();

    }
    public List<ProductResponse> getProducts(String userId)
    {

        ResponseEntity<List<ProductResponse>> responseEntity = productClient.getUserProducts(userId);
        if(responseEntity.getStatusCode() == HttpStatus.OK)
        {
            return responseEntity.getBody();
        }
        throw new ApiRequestException("No products found");
    }

    public ProductResponse getProduct(String data) {
        ResponseEntity<ProductResponse> responseEntity = productClient.getUserProduct(data);
        if(responseEntity.getStatusCode() == HttpStatus.OK)
        {
            return responseEntity.getBody();
        }
        throw new ApiRequestException("No products found");

    }


    public void deleteProduct(String productId) {
        productClient.deleteProduct(productId);

    }

}

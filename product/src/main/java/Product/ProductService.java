package Product;


import clients.Bot.BotClient;
import clients.Exception.ApiRequestException;
import clients.product.ProductRequest;
import clients.product.ProductResponse;
import clients.user.UserClient;

import clients.user.UserRequest;
import com.pricedrop.amqp.RabbitMQMessageProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserClient userClient;
    private final BotClient botClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    public ProductResponse addProduct(ProductRequest productRequest) {

        if(validateUrl(productRequest.url()))
        {
            String userId = addUser(productRequest.UserFirstName(), productRequest.userId().toString(), productRequest.userChatId());
            Product RepoProduct = productRepository.findByUrl(productRequest.url().toString());
            Product product = addUserToProduct(userId,RepoProduct, productRequest.url());
            ProductResponse productResponse = mapProductToDto(product);
            return  productResponse;

        }
        throw new ApiRequestException("Please enter valid Amazon url");

    }
    private Product addUserToProduct(String userId, Product product,String url)
    {
            if(product != null)
            {
                if(product.userId.equals(userId))
                {
                    throw new ApiRequestException("Item already in user's list");
                }
                else
                {

                    product.userId = (userId);

                    return productRepository.save(product);

                }
            }
            Product newProduct = getProduct(url);
            newProduct.userId = (userId);

            return productRepository.save(newProduct);

    }


    private String addUser(String name, String userId, String userChatId)
    {
        UserRequest userRequest = new UserRequest(name, userId, userChatId);

        return userClient.addUser(userRequest);
    }
    private boolean validateUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
            Elements productTitleElem = doc.select("#productTitle");
            if (productTitleElem.size() == 0) {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<ProductResponse> getProducts(String userId) {
        List<Product> userProducts = productRepository.findByUserId(userId);
        if(userProducts == null)
        {
            throw new ApiRequestException("No Items for " + userId);

        }
        List<ProductResponse> productResponses;
        productResponses = userProducts.stream()
                .map(this::mapProductToDto)
                .collect(Collectors.toList());
        return productResponses;

    }

    public ProductResponse getUserProduct(String productId) {

        Product product = productRepository.findById(Integer.parseInt(productId)).orElseThrow();
        ProductResponse productResponse = mapProductToDto(product);

        return productResponse;
    }

    public void deleteProduct(String productId) {
        productRepository.deleteById(Integer.parseInt(productId));
    }

    public  ProductResponse mapProductToDto(Product product)
    {
        return new ProductResponse(product.getId(),product.getTitle(),product.getUserId(),product.getCurrentPrice(),product.getUrl());
    }

    public static Product getProduct(String url)
    {
        Document doc;
        Product product = null;
        try {
            if(url.contains("language=he_IL"))
            {
                url.replace("language=he_IL&currency=ILS","language=en_US&currency=USD");
            }
            else {

                url +="&language=en_US&currency=USD";
            }
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
            Elements productTitleElem = doc.select("#productTitle");
            Elements priceSpan = doc.select(".a-price-whole");
            Elements decimalPrice = doc.select(".a-price-fraction");
            String productTitle = productTitleElem.first().text();
            String[] title = productTitle.split(" ");
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Double price = 0.0;

            if (!priceSpan.isEmpty()) {
                try {
                    price = format.parse(priceSpan.first().text()).doubleValue();
                    String decimalPriceString = "0." + format.parse(decimalPrice.first().text());
                    price += Double.parseDouble(decimalPriceString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            product = Product.builder().currentPrice(price).title(title[0] +" " + title[1] +" " + title[2] +" " + title[3]).url(url).build();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return product;
    }
    @Async
    public void track(Product product)
    {
        log.info(Thread.currentThread().getName());
        Product testProduct = getProduct(product.getUrl());
        if(testProduct.getCurrentPrice() <= product.getCurrentPrice())
        {
            sendNotification(product);
        }

    }
    private void sendNotification(Product product) {
        rabbitMQMessageProducer.publish(product,"internal.exchange","internal.notification.routing-key");

    }

    @Component
    public class ProductTrackerJob {
        @Autowired
        public ProductRepository productRepository;

        @Scheduled(cron = "0 * * * * *")
        public void scrapeProducts() {
            List<Product> products = productRepository.findAll();
            for (Product product : products) {
                track(product);
            }
        }
    }

}

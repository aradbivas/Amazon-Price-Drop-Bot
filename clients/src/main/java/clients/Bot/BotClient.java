package clients.Bot;

import clients.product.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "bot"
)
public interface BotClient {
    @PostMapping("api/v1/bot/notify")
    void sendNotification(@RequestBody ProductResponse productResponse);
}

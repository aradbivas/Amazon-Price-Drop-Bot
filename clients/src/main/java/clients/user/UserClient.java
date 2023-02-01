package clients.user;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user"
)

public interface UserClient {
    @PostMapping("api/v1/user")
    String addUser(@RequestBody UserRequest userRequest);
}

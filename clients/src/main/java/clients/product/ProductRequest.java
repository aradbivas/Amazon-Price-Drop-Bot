package clients.product;

import lombok.AllArgsConstructor;

public record ProductRequest(Long userId, String UserFirstName, String url, String userChatId){

}


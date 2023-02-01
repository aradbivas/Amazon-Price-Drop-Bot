package clients.product;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ProductResponse {

    Integer id;
    String title;
    String userId;
    Double currentPrice;
    String url;


}

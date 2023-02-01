package clients.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "product"
)

public interface ProductClient {
    @PostMapping("api/v1/product/addproduct")
     ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest);

    @PostMapping("api/v1/product/getuserproducts/")
    ResponseEntity<List<ProductResponse>> getUserProducts(@RequestBody String userId);

    @PostMapping("api/v1/product/getproduct")
    ResponseEntity<ProductResponse> getUserProduct(@RequestBody String productId);

    @DeleteMapping("api/v1/product/deleteproduct")
    void deleteProduct(@RequestBody String productId);
}

package Product;

import clients.product.ProductRequest;
import clients.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/product")
@Slf4j
@AllArgsConstructor
@EnableScheduling
public class ProductController {

    private final ProductService productService;
    @PostMapping("/addproduct")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest)
    {
        ResponseEntity<ProductResponse> responseEntity;

        try
        {
            ProductResponse response = productService.addProduct(productRequest);
            responseEntity = new ResponseEntity(response, HttpStatus.OK);
        }
        catch (Exception ex)
        {
            responseEntity = new ResponseEntity(ex, HttpStatus.BAD_REQUEST);
        }

        return responseEntity;

    }
    @PostMapping("/getuserproducts/")
    public ResponseEntity<?> getUserProducts(@RequestBody String userId)
    {
        ResponseEntity responseEntity;

        try{
            List<ProductResponse> response = productService.getProducts(userId);
            responseEntity = new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception ex)
        {
            responseEntity = new ResponseEntity(ex, HttpStatus.BAD_REQUEST);

        }

        return responseEntity;

    }
    @PostMapping("/getproduct")
    ResponseEntity<ProductResponse> getUserProduct(@RequestBody String productId)
    {
        ResponseEntity<ProductResponse> responseEntity;

        try
        {
            ProductResponse product = productService.getUserProduct(productId);
            responseEntity = new ResponseEntity(product,HttpStatus.OK);


        }
        catch (Exception ex)
        {
            responseEntity = new ResponseEntity(ex, HttpStatus.BAD_REQUEST);

        }
        return responseEntity;
    }
    @DeleteMapping("/deleteproduct")
    void deleteProduct(@RequestBody String productId)
    {
        productService.deleteProduct(productId);
    }
}

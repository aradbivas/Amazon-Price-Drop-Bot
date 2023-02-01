package Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor

public class Product {

    @Id
    @SequenceGenerator(
            name = "product_id_sequence",
            sequenceName = "product_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_id_sequence"

    )
    Integer id;
    @Column(name = "title", length = 1024)

    String title;
    String userId;
    Double currentPrice;
    @Column(name = "url", length = 1024)
    String url;


}

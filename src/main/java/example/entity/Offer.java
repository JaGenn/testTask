package example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    private Integer id;
    private boolean available;
    private String url;
    private BigDecimal price;
    private String picture;
    private String name;
    private String vendor;
    private String description;
    private Integer count;
    private String vendorCode;
    private Integer categoryId;
    private String currencyId;
}
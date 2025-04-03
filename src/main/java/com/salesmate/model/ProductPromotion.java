package com.salesmate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
ProductPromotion Schema in oracle
    product_id INT NOT NULL,
    promotion_id INT NOT NULL,
    PRIMARY KEY (product_id, promotion_id),
    FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id),
    FOREIGN KEY (promotion_id) REFERENCES PROMOTION(promotion_id)
);
*/

@Entity
@Table(name = "product_promotion")
public class ProductPromotion {
    @Id
    private int productId;

    @Id
    private int promotionId;

    // Constructors
    public ProductPromotion() {
    }

    public ProductPromotion(int productId, int promotionId) {
        this.productId = productId;
        this.promotionId = promotionId;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    // Optionally, you can implement the equals and hashCode methods based on productId and promotionId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductPromotion that = (ProductPromotion) o;

        if (productId != that.productId) return false;
        return promotionId == that.promotionId;
    }

    @Override
    public int hashCode() {
        int result = productId;
        result = 31 * result + promotionId;
        return result;
    }
}

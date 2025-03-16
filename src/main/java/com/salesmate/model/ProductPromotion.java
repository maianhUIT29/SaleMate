package com.salesmate.model;

import jakarta.persistence.*;

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
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    // Optionally, you can implement the equals and hashCode methods based on product_id and promotion_id 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductPromotion that = (ProductPromotion) o;

        if (!product.equals(that.product)) return false;
        return promotion.equals(that.promotion);
    }

    @Override
    public int hashCode() {
        int result = product.hashCode();
        result = 31 * result + promotion.hashCode();
        return result;
    }
}

package com.yigit.productCRUD.models;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "ProductStatus")
public class ProductStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name = "status")
    private String status;
    @Column(name = "message")
    private String message;

    public ProductStatus(Product product, String status, String message) {
        this.product = product;
        this.status = status;
        this.message = message;
    }
}

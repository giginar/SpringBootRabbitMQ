package com.yigit.productCRUD.Service;

import com.yigit.productCRUD.models.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();
    List<Product> getProductByTitle(String title);
    Optional<Product> getProductById(long id);
    void createProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(long id);
    void deleteAllProducts();
    List<Product> findByPublished(boolean b);

}

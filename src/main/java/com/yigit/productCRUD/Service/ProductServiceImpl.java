package com.yigit.productCRUD.Service;

import com.yigit.productCRUD.models.Product;
import com.yigit.productCRUD.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        return products;
    }

    @Override
    public List<Product> getProductByTitle(String title) {
        List<Product> products = new ArrayList<>();
        productRepository.findByTitleContaining(title).forEach(products::add);
        return products;
    }

    @Override
    public Optional<Product> getProductById(long id) {
        Optional<Product> product = productRepository.findById(id);
        return product;
    }

    @Override
    public void createProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    @Override
    public List<Product> findByPublished(boolean isPublished) {
        List<Product> products = productRepository.findByPublished(isPublished);
        if (products.isEmpty()) {
            return null;
        }else {
            return products;
        }
    }
}

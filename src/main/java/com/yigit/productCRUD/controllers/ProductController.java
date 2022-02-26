package com.yigit.productCRUD.controllers;

import com.yigit.productCRUD.Service.ProductService;
import com.yigit.productCRUD.config.MessagingConfig;
import com.yigit.productCRUD.models.Product;
import com.yigit.productCRUD.models.ProductStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Product controller.
 */
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("")
public class ProductController {

    private static final Logger log = LogManager.getLogger(ProductController.class);

    /**
     * The Product service.
     */
    @Autowired
    ProductService productService;
    /**
     * The Rabbit template.
     */
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * Gets all products.
     *
     * @param title the title
     * @return the all products
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(required = false) String title) {
        try {
            List<Product> products = new ArrayList<>();
            if (title == null){
                products = productService.getAllProducts();
                for (Product product: products) {
                    ProductStatus productStatus = new ProductStatus(product,"ACTIVE","Product found successfully");
                    rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,productStatus);
                }
            } else{
                products = productService.getProductByTitle(title);
                for (Product product: products) {
                    ProductStatus productStatus = new ProductStatus(product,"ACTIVE","Product found successfully");
                    rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,productStatus);
                }
            }
            if (products.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets product by id.
     *
     * @param id the id
     * @return the product by id
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            ProductStatus productStatus = new ProductStatus(product.get(),"ACTIVE","Product found successfully");
            rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,productStatus);
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            log.info("::getProductById id not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create product response entity.
     *
     * @param product the product
     * @return the response entity
     */
    @PostMapping("/products")
    public ResponseEntity<String > createProduct(@RequestBody Product product) {
        try {
            productService.createProduct(new Product(product.getTitle(), product.getDescription(), product.isPublished()));
            ProductStatus productStatus = new ProductStatus(product,"CREATING","Product created successfully");
            rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,productStatus);
            return new ResponseEntity<>("Product was created successfully.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update product response entity.
     *
     * @param id      the id
     * @param product the product
     * @return the response entity
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") long id, @RequestBody Product product) {
        Optional<Product> productData = productService.getProductById(id);
        if (productData.isPresent()) {
            ProductStatus productStatus = new ProductStatus(product,"UPDATING","Product updated successfully");
            rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,productStatus);
            Product updatedProduct = productData.get();
            updatedProduct.setTitle(product.getTitle());
            updatedProduct.setDescription(product.getDescription());
            updatedProduct.setPublished(product.isPublished());
            productService.updateProduct(updatedProduct);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete product response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") long id) {
        try {
            ProductStatus productStatus = new ProductStatus(productService.getProductById(id).get(),"DELETING","Product deleted successfully");
            rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,productStatus);
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete all products response entity.
     *
     * @return the response entity
     */
    @DeleteMapping("/products")
    public ResponseEntity<HttpStatus> deleteAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            for (Product product: products) {
                ProductStatus productStatus = new ProductStatus(product,"DELETING","Product deleted successfully");
                rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,productStatus);
            }
            productService.deleteAllProducts();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Find by published response entity.
     *
     * @return the response entity
     */
    @GetMapping("/products/published")
    public ResponseEntity<List<Product>> findByPublished() {
        try {
            List<Product> products = productService.findByPublished(true);
            for (Product product: products) {
                ProductStatus productStatus = new ProductStatus(product,"PUBLISHED","Published product found successfully");
                rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,productStatus);
            }
            if (products.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

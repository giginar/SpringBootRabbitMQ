package com.yigit.productCRUD.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yigit.productCRUD.models.Product;
import com.yigit.productCRUD.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * The type Product service impl test.
 */
class ProductServiceImplTest {
    /**
     * Test constructor.
     */
    @Test
    void testConstructor() {
        ProductRepository productRepository = mock(ProductRepository.class);
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        assertTrue((new ProductServiceImpl(productRepository, rabbitTemplate)).getAllProducts().isEmpty());
        assertNull(rabbitTemplate.getAfterReceivePostProcessors());
        assertFalse(rabbitTemplate.isUsePublisherConnection());
        assertFalse(rabbitTemplate.isRunning());
        assertTrue(rabbitTemplate.isReturnListener());
        assertFalse(rabbitTemplate.isConfirmListener());
        assertFalse(rabbitTemplate.isChannelTransacted());
        assertEquals(0, rabbitTemplate.getUnconfirmedCount());
        assertEquals("", rabbitTemplate.getRoutingKey());
        assertTrue(rabbitTemplate
                .getMessageConverter() instanceof org.springframework.amqp.support.converter.SimpleMessageConverter);
        assertEquals("", rabbitTemplate.getExchange());
        assertEquals("UTF-8", rabbitTemplate.getEncoding());
        assertNull(rabbitTemplate.getConnectionFactory());
    }

    /**
     * Test get all products.
     */
    @Test
    void testGetAllProducts() {
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(new ArrayList<Product>());
        assertTrue((new ProductServiceImpl(productRepository, new RabbitTemplate())).getAllProducts().isEmpty());
        verify(productRepository).findAll();
    }

    /**
     * Test get product by title.
     */
    @Test
    void testGetProductByTitle() {
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findByTitleContaining((String) any())).thenReturn(new ArrayList<Product>());
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository, new RabbitTemplate());
        assertTrue(productServiceImpl.getProductByTitle("Title").isEmpty());
        verify(productRepository).findByTitleContaining((String) any());
        assertTrue(productServiceImpl.getAllProducts().isEmpty());
    }

    /**
     * Test get product by id.
     */
    @Test
    void testGetProductById() {
        Product product = new Product();
        product.setPublished(true);
        product.setTitle("Title");
        product.setDescription("Some Description");
        Optional<Product> ofResult = Optional.<Product>of(product);
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findById((Long) any())).thenReturn(ofResult);
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository, new RabbitTemplate());
        Optional<Product> actualProductById = productServiceImpl.getProductById(123L);
        assertSame(ofResult, actualProductById);
        assertTrue(actualProductById.isPresent());
        verify(productRepository).findById((Long) any());
        assertTrue(productServiceImpl.getAllProducts().isEmpty());
    }

    /**
     * Test create product.
     */
    @Test
    void testCreateProduct() {
        Product product = new Product();
        product.setPublished(true);
        product.setTitle("Title");
        product.setDescription("Some Description");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.save((Product) any())).thenReturn(product);
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository, new RabbitTemplate());

        Product product1 = new Product();
        product1.setPublished(true);
        product1.setTitle("Title");
        product1.setDescription("Some Description");
        productServiceImpl.createProduct(product1);
        verify(productRepository).save((Product) any());
        assertTrue(productServiceImpl.getAllProducts().isEmpty());
    }

    /**
     * Test update product.
     */
    @Test
    void testUpdateProduct() {
        Product product = new Product();
        product.setPublished(true);
        product.setTitle("Title");
        product.setDescription("Some Description");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.save((Product) any())).thenReturn(product);
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository, new RabbitTemplate());

        Product product1 = new Product();
        product1.setPublished(true);
        product1.setTitle("Title");
        product1.setDescription("Some Description");
        productServiceImpl.updateProduct(product1);
        verify(productRepository).save((Product) any());
        assertTrue(productServiceImpl.getAllProducts().isEmpty());
    }

    /**
     * Test delete product.
     */
    @Test
    void testDeleteProduct() {
        ProductRepository productRepository = mock(ProductRepository.class);
        doNothing().when(productRepository).deleteById((Long) any());
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository, new RabbitTemplate());
        productServiceImpl.deleteProduct(123L);
        verify(productRepository).deleteById((Long) any());
        assertTrue(productServiceImpl.getAllProducts().isEmpty());
    }

    /**
     * Test delete all products.
     */
    @Test
    void testDeleteAllProducts() {
        ProductRepository productRepository = mock(ProductRepository.class);
        doNothing().when(productRepository).deleteAll();
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository, new RabbitTemplate());
        productServiceImpl.deleteAllProducts();
        verify(productRepository).deleteAll();
        assertTrue(productServiceImpl.getAllProducts().isEmpty());
    }

    /**
     * Test find by published.
     */
    @Test
    void testFindByPublished() {
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findByPublished(anyBoolean())).thenReturn(new ArrayList<Product>());
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository, new RabbitTemplate());
        assertNull(productServiceImpl.findByPublished(true));
        verify(productRepository).findByPublished(anyBoolean());
        assertTrue(productServiceImpl.getAllProducts().isEmpty());
    }

    /**
     * Test find by published 2.
     */
    @Test
    void testFindByPublished2() {
        Product product = new Product();
        product.setPublished(true);
        product.setTitle("Title");
        product.setDescription("Some Description");

        ArrayList<Product> productList = new ArrayList<Product>();
        productList.add(product);
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findByPublished(anyBoolean())).thenReturn(productList);
        ProductServiceImpl productServiceImpl = new ProductServiceImpl(productRepository, new RabbitTemplate());
        List<Product> actualFindByPublishedResult = productServiceImpl.findByPublished(true);
        assertSame(productList, actualFindByPublishedResult);
        assertEquals(1, actualFindByPublishedResult.size());
        verify(productRepository).findByPublished(anyBoolean());
        assertTrue(productServiceImpl.getAllProducts().isEmpty());
    }
}


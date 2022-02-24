package com.yigit.productCRUD.listener;

import com.yigit.productCRUD.config.MessagingConfig;
import com.yigit.productCRUD.models.Product;
import com.yigit.productCRUD.models.ProductStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductMessageListener {

    private static final Logger log = LogManager.getLogger(ProductMessageListener.class);

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeMessageFromQueue(ProductStatus productStatus) {
        log.info("Message received from queue : " + productStatus);
    }

}

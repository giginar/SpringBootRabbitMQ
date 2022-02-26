package com.yigit.productCRUD.listener;

import com.yigit.productCRUD.config.MessagingConfig;
import com.yigit.productCRUD.models.LoginStatus;
import com.yigit.productCRUD.models.ProductStatus;
import com.yigit.productCRUD.models.RegistrationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    private static final Logger log = LogManager.getLogger(MessageListener.class);

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeProductMessageFromQueue(ProductStatus productStatus) {
        log.info("Message received from queue : " + productStatus);
    }

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeLoginMessageFromQueue(LoginStatus loginStatus) {
        log.info("Message received from queue : " + loginStatus);
    }

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeRegistrationMessageFromQueue(RegistrationStatus registrationStatus) {
        log.info("Message received from queue : " + registrationStatus);
    }

}

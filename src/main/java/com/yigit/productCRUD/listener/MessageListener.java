package com.yigit.productCRUD.listener;

import com.yigit.productCRUD.models.ProjectConstants;
import com.yigit.productCRUD.models.LoginStatus;
import com.yigit.productCRUD.models.ProductStatus;
import com.yigit.productCRUD.models.RegistrationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * The type Message listener.
 */
@Component
public class MessageListener {

    private static final Logger log = LogManager.getLogger(MessageListener.class);

    /**
     * Consume product message from queue.
     *
     * @param productStatus the product status
     */
    @RabbitListener(queues = ProjectConstants.QUEUE)
    public void consumeProductMessageFromQueue(ProductStatus productStatus) {
        log.info("Message received from queue : " + productStatus);
    }

    /**
     * Consume login message from queue.
     *
     * @param loginStatus the login status
     */
    @RabbitListener(queues = ProjectConstants.QUEUE)
    public void consumeLoginMessageFromQueue(LoginStatus loginStatus) {
        log.info("Message received from queue : " + loginStatus);
    }

    /**
     * Consume registration message from queue.
     *
     * @param registrationStatus the registration status
     */
    @RabbitListener(queues = ProjectConstants.QUEUE)
    public void consumeRegistrationMessageFromQueue(RegistrationStatus registrationStatus) {
        log.info("Message received from queue : " + registrationStatus);
    }

}

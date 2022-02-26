package com.yigit.productCRUD.models;

import com.yigit.productCRUD.requests.registration.RegistrationRequest;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStatus {

    private RegistrationRequest registrationRequest;
    private String status;
    private String message;
}

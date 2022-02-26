package com.yigit.productCRUD.models;

import com.yigit.productCRUD.requests.login.LoginRequest;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginStatus {
    private LoginRequest loginRequest;
    private String status;
    private String message;
}

package com.yorimichi.yorimichi.global.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoAccountService {

    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${cognito.user-pool-id}")
    private String userPoolId;

    public void disableUser(String email) {
        try {
            cognitoClient.adminDisableUser(request -> request
                    .userPoolId(userPoolId)
                    .username(email));
        } catch (SdkException exception) {
            log.error("Failed to disable Cognito user", exception);
            throw new CustomException(ErrorCode.COGNITO_ACCOUNT_UPDATE_FAILED);
        }
    }

    public void enableUser(String email) {
        try {
            cognitoClient.adminEnableUser(request -> request
                    .userPoolId(userPoolId)
                    .username(email));
        } catch (SdkException exception) {
            log.error("Failed to enable Cognito user", exception);
            throw new CustomException(ErrorCode.COGNITO_ACCOUNT_UPDATE_FAILED);
        }
    }
}

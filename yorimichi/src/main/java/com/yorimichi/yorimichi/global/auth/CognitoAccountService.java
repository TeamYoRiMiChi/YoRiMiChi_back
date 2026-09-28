package com.yorimichi.yorimichi.global.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoAccountService {

    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${cognito.user-pool-id}")
    private String userPoolId;

    public String getVerifiedEmail(String username) {
        if (username == null || username.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        try {
            var response = cognitoClient.adminGetUser(request -> request
                    .userPoolId(userPoolId)
                    .username(username));

            String email = attributeValue(response.userAttributes(), "email");
            boolean emailVerified = Boolean.parseBoolean(
                    attributeValue(response.userAttributes(), "email_verified")
            );

            if (email == null || !emailVerified) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            return email;
        } catch (CustomException exception) {
            throw exception;
        } catch (SdkException exception) {
            log.error("Failed to read Cognito user attributes", exception);
            throw new CustomException(ErrorCode.COGNITO_ACCOUNT_UPDATE_FAILED);
        }
    }

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

    private String attributeValue(
            java.util.List<AttributeType> attributes,
            String attributeName
    ) {
        return attributes.stream()
                .filter(attribute -> attributeName.equals(attribute.name()))
                .map(AttributeType::value)
                .findFirst()
                .orElse(null);
    }
}

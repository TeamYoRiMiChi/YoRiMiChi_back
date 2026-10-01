package com.yorimichi.yorimichi.global.config;

import org.springframework.context.annotation.Profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
@Profile("!local")
public class CognitoClientConfig {

    // The SDK obtains credentials from the default AWS credential chain.
    // On ECS, this means the task role configured in Terraform.
    @Bean(destroyMethod = "close")
    public CognitoIdentityProviderClient cognitoIdentityProviderClient(
            @Value("${cognito.region}") String region
    ) {
        return CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .build();
    }
}

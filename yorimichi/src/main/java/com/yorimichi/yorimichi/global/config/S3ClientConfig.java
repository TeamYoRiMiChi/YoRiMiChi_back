package com.yorimichi.yorimichi.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * rds(AWS) 프로필에서만 S3Client 빈을 만듭니다.
 *
 * local 프로필일 때는 이 빈 자체가 생성되지 않으므로,
 * AWS 자격증명이나 네트워크가 없어도 로컬 서버 구동에는 영향이 없습니다.
 * (CognitoClientConfig와 같은 패턴입니다.)
 */
@Configuration
@Profile("rds")
public class S3ClientConfig {

    // The SDK obtains credentials from the default AWS credential chain.
    // On ECS, this means the task role configured in Terraform.
    @Bean(destroyMethod = "close")
    public S3Client s3Client(@Value("${app.image.s3.region}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}

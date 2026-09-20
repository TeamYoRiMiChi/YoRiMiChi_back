package com.yorimichi.yorimichi.domain.postal.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.yorimichi.yorimichi.domain.postal.dto.PostalAddressDto;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * 일본 우편번호 → 주소 검색 (zipcloud 외부 API 프록시)
 *
 * https://zipcloud.ibsnet.co.jp/doc/api
 *
 * 프론트가 zipcloud를 직접 호출하지 않고 우리 백엔드를 거치게 해서,
 * CORS나 외부 API 주소가 바뀌어도 프론트 코드는 영향받지 않도록 합니다.
 */
@Service
@RequiredArgsConstructor
public class PostalCodeService {

    private static final String ZIPCLOUD_URL = "https://zipcloud.ibsnet.co.jp/api/search?zipcode=";

    /*
     * zipcloud 응답을 파싱하는 용도로만 쓰는 내부 ObjectMapper입니다.
     * (Spring이 관리하는 ObjectMapper 빈을 주입받지 않고 직접 생성합니다.
     *  Boot 4 환경에서 스프링이 자동 구성하는 ObjectMapper 빈이 이 클래스가
     *  기대하는 com.fasterxml.jackson.databind.ObjectMapper 타입으로 등록되지
     *  않는 경우가 있어, 이 서비스는 자체적으로 인스턴스를 만들어 씁니다.)
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public List<PostalAddressDto> search(String zipcode) {
        String normalized = normalize(zipcode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ZIPCLOUD_URL + normalized))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        JsonNode root;
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            root = objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.POSTAL_LOOKUP_FAILED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.POSTAL_LOOKUP_FAILED);
        }

        JsonNode results = root.path("results");
        if (!results.isArray() || results.isEmpty()) {
            throw new CustomException(ErrorCode.POSTAL_CODE_NOT_FOUND);
        }

        List<PostalAddressDto> addresses = new ArrayList<>();
        for (JsonNode r : results) {
            String address1 = textOrNull(r, "address1");
            String address2 = textOrNull(r, "address2");
            String address3 = textOrNull(r, "address3");

            addresses.add(PostalAddressDto.builder()
                    .zipcode(textOrNull(r, "zipcode"))
                    .prefCode(textOrNull(r, "prefcode"))
                    .address1(address1)
                    .address2(address2)
                    .address3(address3)
                    .kana1(textOrNull(r, "kana1"))
                    .kana2(textOrNull(r, "kana2"))
                    .kana3(textOrNull(r, "kana3"))
                    .fullAddress(nullToEmpty(address1) + nullToEmpty(address2) + nullToEmpty(address3))
                    .build());
        }

        return addresses;
    }

    /** 하이픈이 섞여 와도(123-4567) 숫자 7자리만 추려서 zipcloud에 넘깁니다 */
    private String normalize(String zipcode) {
        if (zipcode == null) {
            throw new CustomException(ErrorCode.POSTAL_CODE_INVALID);
        }

        String digitsOnly = zipcode.replaceAll("[^0-9]", "");
        if (digitsOnly.length() != 7) {
            throw new CustomException(ErrorCode.POSTAL_CODE_INVALID);
        }

        return digitsOnly;
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

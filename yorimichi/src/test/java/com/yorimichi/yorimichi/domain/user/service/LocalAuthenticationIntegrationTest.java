package com.yorimichi.yorimichi.domain.user.service;

import java.net.URI;
import java.net.http.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:local-auth;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.sql.init.mode=always", "spring.sql.init.schema-locations=classpath:local-auth-schema.sql",
    "spring.jpa.hibernate.ddl-auto=none", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.mail.username=", "spring.mail.password=", "spring.batch.job.enabled=false"
})
class LocalAuthenticationIntegrationTest {
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired ApplicationContext context;
    @Autowired PasswordEncoder passwords;
    @Autowired LocalAuthenticationService authentication;
    @Autowired JwtDecoder decoder;
    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> request(String method, String path, String body, String token) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json");
        if (token != null) builder.header("Authorization", "Bearer " + token);
        return client.send(builder.method(method, body == null
                ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void authenticatesWithoutCognitoColumnOrAwsAndProtectsMemberApis() throws Exception {
        assertThat(context.getBeansOfType(CognitoIdentityProviderClient.class)).isEmpty();
        assertThat(request("GET", "/api/auth/config", null, null).body()).contains("\"mode\":\"local\"");
        String signup = "{\"email\":\"local@example.com\",\"password\":\"LocalTest!123\",\"name\":\"Local User\",\"phone\":\"090-1234-5678\"}";
        assertThat(request("POST", "/api/users/signup", signup, null).statusCode()).isEqualTo(200);
        String hash = jdbc.queryForObject("SELECT password FROM MEMBER WHERE email='local@example.com'", String.class);
        assertThat(hash).isNotEqualTo("LocalTest!123");
        assertThat(passwords.matches("LocalTest!123", hash)).isTrue();
        assertThat(request("POST", "/api/users/signup", signup, null).statusCode()).isEqualTo(409);
        assertThat(request("POST", "/api/users/login", "{\"email\":\"local@example.com\",\"password\":\"wrong\"}", null).statusCode()).isEqualTo(401);
        var login = request("POST", "/api/users/login", "{\"email\":\"local@example.com\",\"password\":\"LocalTest!123\"}", null);
        assertThat(login.statusCode()).isEqualTo(200);
        var tokenMatch = java.util.regex.Pattern.compile("\"accessToken\":\"([^\"]+)\"").matcher(login.body());
        assertThat(tokenMatch.find()).isTrue();
        String token = tokenMatch.group(1);
        assertThat(decoder.decode(token).getIssuer().toString()).isEqualTo("http://yorimichi.local");
        assertThat(request("GET", "/api/users/me", null, null).statusCode()).isEqualTo(401);
        var me = request("GET", "/api/users/me", null, token);
        assertThat(me.statusCode()).isEqualTo(200);
        assertThat(me.body()).contains("local@example.com").doesNotContain("password", hash);
        assertThat(request("GET", "/api/myprofile", null, token).statusCode()).isEqualTo(200);
        assertThat(request("GET", "/api/users/me", null, token + "invalid").statusCode()).isEqualTo(401);
        assertThat(request("GET", "/api/admin/users", null, token).statusCode()).isEqualTo(403);
        jdbc.update("UPDATE MEMBER SET status='INACTIVE' WHERE email='local@example.com'");
        assertThat(request("GET", "/api/users/me", null, token).statusCode()).isEqualTo(403);
        assertThatThrownBy(() -> authentication.login(new com.yorimichi.yorimichi.domain.user.dto.LocalLoginRequest("local@example.com", "LocalTest!123")))
                .isInstanceOf(com.yorimichi.yorimichi.global.error.CustomException.class);
    }
}

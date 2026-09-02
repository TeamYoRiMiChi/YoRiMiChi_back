package com.yorimichi.yorimichi.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "入力内容を確認してください。"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "サーバーエラーが発生しました。"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "存在しない会員です。"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "既に使用されているメールアドレスです。"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U003", "パスワードが一致しません。"),
    WITHDRAWN_MEMBER(HttpStatus.FORBIDDEN, "U004", "退会済みのアカウントです。"),
    SUSPENDED_MEMBER(HttpStatus.FORBIDDEN, "U005", "利用が停止されているアカウントです。"),
    SOCIAL_ACCOUNT_ONLY(HttpStatus.BAD_REQUEST, "U006", "ソーシャルログインでご利用ください。"),

    // Auth
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A001", "メールアドレスまたはパスワードが正しくありません。"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "無効なトークンです。"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "トークンの有効期限が切れています。"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A004", "ログインが必要です。");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

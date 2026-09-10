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
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A004", "ログインが必要です。"),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "存在しない商品です。"),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "P002", "在庫が不足しています。"),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "存在しないカテゴリです。"),

    // Cart
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "カートに該当する商品がありません。"),
    CART_EMPTY(HttpStatus.BAD_REQUEST, "T002", "カートに商品がありません。"),

    // Group buy
    GROUP_BUY_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "共同購入が見つかりません。"),
    GROUP_BUY_CLOSED(HttpStatus.CONFLICT, "B002", "この共同購入の募集は終了しています。"),
    ALREADY_PARTICIPATING(HttpStatus.CONFLICT, "B003", "すでにこの共同購入へ申し込んでいます。"),
    GROUP_BUY_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "B004", "申し込み可能な数量を超えています。"),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "存在しない注文です。"),
    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "O002", "配送先を登録してください。"),
    CUSTOMS_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "O003", "個人通関固有符号を登録してください。");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

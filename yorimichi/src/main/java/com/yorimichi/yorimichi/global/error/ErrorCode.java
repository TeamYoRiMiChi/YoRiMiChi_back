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
	ADMIN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "A005","管理者権限が必要です。"),

	// Product
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "存在しない商品です。"),
	OUT_OF_STOCK(HttpStatus.CONFLICT, "P002", "在庫が不足しています。"),

	// Category
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "存在しないカテゴリです。"),
	CATEGORY_IN_USE(HttpStatus.CONFLICT, "G002","使用中のカテゴリーは削除できません。"),

	// Cart
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "カートに該当する商品がありません。"),
	CART_EMPTY(HttpStatus.BAD_REQUEST, "T002", "カートに商品がありません。"),
	DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "G003", "既に登録されているカテゴリー名です。"),

	// Inquiry
	INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "お問い合わせが見つかりません。"),
	INQUIRY_NOT_OWNER(HttpStatus.FORBIDDEN, "I002", "このお問い合わせは編集できません。"),
	INQUIRY_ALREADY_ANSWERED(HttpStatus.CONFLICT, "I003", "回答済みのお問い合わせは編集できません。"),

	// Group buy
	GROUP_BUY_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "共同購入が見つかりません。"),
	GROUP_BUY_CLOSED(HttpStatus.CONFLICT, "B002", "この共同購入の募集は終了しています。"),
	ALREADY_PARTICIPATING(HttpStatus.CONFLICT, "B003", "すでにこの共同購入へ申し込んでいます。"),
	GROUP_BUY_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "B004", "申し込み可能な数量を超えています。"),
	NOT_GROUP_BUY_PRODUCT(HttpStatus.BAD_REQUEST, "B005", "共同購入の商品ではありません。"),
	GROUP_BUY_ALREADY_RECRUITING(HttpStatus.CONFLICT, "B006", "この商品はすでに募集中の共同購入があります。"),
	GROUP_BUY_HAS_PARTICIPANTS(HttpStatus.CONFLICT, "B007", "参加者がいる共同購入は削除できません。"),

	// Order
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "存在しない注文です。"),
	ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "O002", "配送先を登録してください。"),
	CUSTOMS_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "O003", "個人通関固有符号を登録してください。"),

	// Coupon
	COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "存在しないクーポンです。"),
	DUPLICATE_COUPON_CODE(HttpStatus.CONFLICT, "N002", "既に使用されているクーポンコードです。"),
	INVALID_COUPON_ISSUE_REQUEST(HttpStatus.BAD_REQUEST, "N003", "発給対象を指定してください。"),
	COUPON_USAGE_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "N004", "発給可能な残り数量を超えています。"),
	INVALID_COUPON_STATUS_CHANGE(HttpStatus.CONFLICT, "N005", "現在の状態では変更できないクーポンです。"),
	COUPON_ALREADY_CLAIMED(HttpStatus.CONFLICT, "N006", "既に受け取ったクーポンです。"),
	COUPON_NOT_CLAIMABLE(HttpStatus.BAD_REQUEST, "N007", "受け取ることができないクーポンです。"),
	COUPON_MIN_ORDER_AMOUNT_NOT_MET(HttpStatus.BAD_REQUEST, "N008", "最低注文金額に達していないクーポンです。"),
	COUPON_NOT_USABLE(HttpStatus.CONFLICT, "N009", "使用できないクーポンです。"),

	// Postal code (zipcloud 외부 API 프록시)
	POSTAL_CODE_INVALID(HttpStatus.BAD_REQUEST, "Z001", "郵便番号の形式が正しくありません。"),
	POSTAL_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "Z002", "該当する住所が見つかりません。"),
	POSTAL_LOOKUP_FAILED(HttpStatus.BAD_GATEWAY, "Z003", "郵便番号検索に失敗しました。");

	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}


}

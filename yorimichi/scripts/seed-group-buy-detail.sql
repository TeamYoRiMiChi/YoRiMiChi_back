USE yorimichi_db;

-- 기존 나이키 상품은 해외직구용으로 그대로 유지
-- 공동구매에는 아래의 요리미치 상품만 연결

-- 공동구매 전용 시연 상품
-- GROUP_BUY로 두면 해외직구 PRODUCT 목록에는 노출되지 않습니다.
INSERT INTO PRODUCT (
    category_id, product_name, product_name_jp, description,
    price_jpy, original_price_jpy, stock, brand, status, thumbnail_url
)
SELECT 1, '요리미치 말차 모찌 세트', 'よりみち抹茶もちセット',
       '京都産の抹茶を使った、やわらかなもちの詰め合わせです。',
       3980, 6500, 100, 'YORIMICHI', 'GROUP_BUY', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM PRODUCT WHERE product_name = '요리미치 말차 모찌 세트'
);

INSERT INTO PRODUCT (
    category_id, product_name, product_name_jp, description,
    price_jpy, original_price_jpy, stock, brand, status, thumbnail_url
)
SELECT 1, '요리미치 유자 카스텔라', 'よりみち柚子カステラ',
       '国産柚子の香りとしっとりした食感を楽しめるカステラです。',
       2780, 4000, 80, 'YORIMICHI', 'GROUP_BUY', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM PRODUCT WHERE product_name = '요리미치 유자 카스텔라'
);

INSERT INTO GROUP_BUY (
    product_id, creator_id, title, description,
    target_quantity, current_quantity, start_date, end_date, status
)
SELECT p.product_id, m.member_id, 'よりみち抹茶もち共同購入',
       p.description, 50, 45, NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 'RECRUITING'
FROM PRODUCT p
CROSS JOIN (SELECT MIN(member_id) AS member_id FROM MEMBER) m
WHERE p.product_name = '요리미치 말차 모찌 세트'
  AND m.member_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM GROUP_BUY gb WHERE gb.product_id = p.product_id)
LIMIT 1;

-- 세 번째 상세조회용 사쿠라 모찌 상품과 공동구매입니다.
INSERT INTO PRODUCT (
    category_id, product_name, product_name_jp, description,
    price_jpy, original_price_jpy, stock, brand, status, thumbnail_url
)
SELECT 1, '요리미치 사쿠라 모찌', 'よりみち桜もち',
       '桜の香りとやわらかな食感を楽しめるもちの詰め合わせです。',
       3200, 4800, 70, 'YORIMICHI', 'GROUP_BUY', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM PRODUCT WHERE product_name = '요리미치 사쿠라 모찌'
);

INSERT INTO GROUP_BUY (
    product_id, creator_id, title, description,
    target_quantity, current_quantity, start_date, end_date, status
)
SELECT p.product_id, m.member_id, 'よりみち桜もち共同購入',
       p.description, 40, 18, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'RECRUITING'
FROM PRODUCT p
CROSS JOIN (SELECT MIN(member_id) AS member_id FROM MEMBER) m
WHERE p.product_name = '요리미치 사쿠라 모찌'
  AND m.member_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM GROUP_BUY gb WHERE gb.product_id = p.product_id)
LIMIT 1;

INSERT INTO GROUP_BUY (
    product_id, creator_id, title, description,
    target_quantity, current_quantity, start_date, end_date, status
)
SELECT p.product_id, m.member_id, 'よりみち柚子カステラ共同購入',
       p.description, 30, 12, NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), 'RECRUITING'
FROM PRODUCT p
CROSS JOIN (SELECT MIN(member_id) AS member_id FROM MEMBER) m
WHERE p.product_name = '요리미치 유자 카스텔라'
  AND m.member_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM GROUP_BUY gb WHERE gb.product_id = p.product_id)
LIMIT 1;

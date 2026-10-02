-- Run only against your local MySQL (yorimichi_db).
-- REVIEW 테이블이 이미 있으면 아무 것도 하지 않습니다 (IF NOT EXISTS).
-- RDS의 REVIEW 테이블과 같은 컬럼 구성입니다.
USE yorimichi_db;

CREATE TABLE IF NOT EXISTS REVIEW (
    review_id     BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT      NOT NULL,
    product_id    BIGINT      NOT NULL,
    sale_type     VARCHAR(20) NOT NULL DEFAULT 'OVERSEAS',
    order_item_id BIGINT      NOT NULL,
    rating        TINYINT     NOT NULL,
    content       TEXT        NULL,
    created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_review_order_item (order_item_id),
    KEY idx_review_product (product_id),
    KEY idx_review_member (member_id),
    CONSTRAINT fk_review_member     FOREIGN KEY (member_id)     REFERENCES MEMBER (member_id),
    CONSTRAINT fk_review_product    FOREIGN KEY (product_id)    REFERENCES PRODUCT (product_id),
    CONSTRAINT fk_review_order_item FOREIGN KEY (order_item_id) REFERENCES ORDER_ITEM (order_item_id)
);

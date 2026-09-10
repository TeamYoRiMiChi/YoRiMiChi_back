-- 로컬 yorimichi_db의 이전 상품 분류를 공통 sale_type 기준으로 변환
-- 실행 전 PRODUCT와 GROUP_BUY 백업 필요
USE yorimichi_db;

-- 컬럼이 없을 때만 추가
SET @has_sale_type = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'product' AND column_name = 'sale_type'
);
SET @sale_type_ddl = IF(@has_sale_type = 0,
    'ALTER TABLE PRODUCT ADD COLUMN sale_type VARCHAR(20) NOT NULL DEFAULT ''OVERSEAS''',
    'SELECT ''sale_type already exists'' AS migration_info');
PREPARE sale_type_stmt FROM @sale_type_ddl;
EXECUTE sale_type_stmt;
DEALLOCATE PREPARE sale_type_stmt;

-- 이전 공동구매 표시만 변환. 상품 번호와 모집 정보 유지
START TRANSACTION;
UPDATE PRODUCT
SET sale_type = 'GROUP_BUY', status = 'ACTIVE'
WHERE status = 'GROUP_BUY';
COMMIT;

SELECT product_id, product_name, sale_type, status
FROM PRODUCT ORDER BY product_id;

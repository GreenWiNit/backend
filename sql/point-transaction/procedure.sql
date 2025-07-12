DELIMITER $

DROP PROCEDURE IF EXISTS GeneratePointTransactions$

CREATE PROCEDURE GeneratePointTransactions(IN count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_id INT;
    DECLARE point_amount DECIMAL(38,2);
    DECLARE balance_after DECIMAL(38,2);
    DECLARE transaction_type ENUM('EARN','SPEND');
    DECLARE target_type ENUM('CHALLENGE','EVENT','EXCHANGE');
    DECLARE description_text VARCHAR(255);
    DECLARE target_id BIGINT;
    DECLARE created_date DATETIME(6);
    DECLARE modified_date DATETIME(6);
    
    WHILE i <= count DO
        SET user_id = FLOOR(1 + RAND() * 10000);
        SET point_amount = ROUND(1000 + RAND() * 4000, 2);
        SET balance_after = ROUND(RAND() * 50000, 2);
        SET created_date = DATE_SUB(NOW(6), INTERVAL FLOOR(RAND() * 365) DAY);
        SET modified_date = created_date;

        IF RAND() > 0.3 THEN
            SET transaction_type = 'EARN';
            SET target_type = 'CHALLENGE';
            SET description_text = CONCAT('챌린지 ', FLOOR(1 + RAND() * 100), ' 적립');
ELSE
            SET transaction_type = 'SPEND';
            SET target_type = 'EXCHANGE';
            SET description_text = CONCAT('상품 ', FLOOR(1 + RAND() * 100), ' 교환');
END IF;
        
        SET target_id = FLOOR(1 + RAND() * 100);

INSERT INTO POINT_TRANSACTIONS
(BALANCE_AFTER, POINT_AMOUNT, CREATED_DATE, MEMBER_ID, MODIFIED_DATE, TARGET_ID, DESCRIPTION, TARGET_TYPE, TYPE)
VALUES (balance_after, point_amount, created_date, user_id, modified_date, target_id, description_text, target_type,
        transaction_type);

IF i % 10000 = 0 THEN
SELECT CONCAT('진행: ', i, ' / ', count) as progress;
END IF;

        SET i = i + 1;

END WHILE;

SELECT CONCAT(count, '건 데이터 생성 완료') as result;
END$

DELIMITER ;

CALL GeneratePointTransactions(500000);
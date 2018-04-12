



DROP PROCEDURE IF EXISTS getInformation;
DROP PROCEDURE IF EXISTS confirmProviderOrder;
DROP PROCEDURE IF EXISTS confirmCustomerOrder;

DELIMITER $$

CREATE PROCEDURE getInformation(IN login varchar(40), IN passw char(40), OUT result TINYINT(2), OUT booksNumber int unsigned,
								OUT copiesNumber int unsigned, OUT customersNumber int unsigned, OUT providersNumber int unsigned, OUT email varchar(40))
BEGIN
	IF login NOT REGEXP '[a-zA-Z0-9]*' OR passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*' OR (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Admin')
    THEN
		SET result=1;
		SET booksNumber= 0;
        SET copiesNumber= 0;
        SET customersNumber= 0;
        SET providersNumber= 0;
        SET email= 'ERROR';
    ELSE
        SET result=0;
        SET booksNumber= (SELECT COUNT(*) FROM Books);
        SET copiesNumber= (SELECT COUNT(*) FROM BookCopies);
        SET customersNumber= (SELECT COUNT(*) FROM Accounts WHERE typee='Customer');
        SET providersNumber= (SELECT COUNT(*) FROM Accounts WHERE typee='Provider');
        SET email= (SELECT email FROM Accounts WHERE typee='Admin' AND Accounts.login=login AND Accounts.passw=passw);
	END IF;
END
$$

CREATE PROCEDURE confirmProviderOrder(IN login varchar(40), IN passw varchar(40), OUT result TINYINT(2), IN provOrder int unsigned)
BEGIN
	IF login NOT REGEXP '[a-zA-Z0-9]*' OR passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*' OR (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Admin')
    THEN
		SET result=1;
    ELSE
		SET result=2;
		SET autocommit = 0;
		START TRANSACTION;
		BEGIN
			IF NOT EXISTS (SELECT * FROM ProviderOrders WHERE ID=provOrder)
			THEN
				ROLLBACK;
			END IF;
            
            SELECT ISBN, warehouse, amount INTO @isbn, @wh, @am FROM ProviderOrders WHERE ID=provOrder;
            
            WHILE @am>0 DO
				INSERT INTO BookCopies(ISBN, warehouse) VALUE (@isbn, @wh);
				SET @am=@am-1;
			END WHILE;
            
			DELETE FROM ProviderOrders
            WHERE ID=provOrder;
                
			SET result=0;
		END;
		COMMIT;
	END IF;
END
$$

CREATE PROCEDURE confirmCustomerOrder(IN login varchar(40), IN passw varchar(40), OUT result TINYINT(2), IN custOrder int unsigned)
BEGIN
	IF login NOT REGEXP '[a-zA-Z0-9]*' OR passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*' OR (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Admin')
    THEN
		SET result=1;
    ELSE
		SET result=2;
		SET autocommit = 0;
		START TRANSACTION;
		BEGIN
			IF NOT EXISTS (SELECT * FROM CustomerOrders WHERE ID=custOrder) OR NOT EXISTS (SELECT * FROM BooksAndOrders WHERE customerOrder=custOrder)
			THEN
				ROLLBACK;
			END IF;
            
            DELETE FROM BookCopies 
            WHERE ID IN (SELECT book FROM BooksAndOrders WHERE customerOrder=custOrder);
            
            DELETE FROM BooksAndOrders
            WHERE customerOrder=custOrder;
            
			DELETE FROM CustomerOrders
            WHERE ID=custOrder;
                
			SET result=0;
		END;
		COMMIT;
	END IF;
END
$$



DELIMITER ;



GRANT EXECUTE ON PROCEDURE Bookshop.getInformation TO 'Admin';
GRANT EXECUTE ON PROCEDURE Bookshop.confirmProviderOrder TO 'Admin';
GRANT EXECUTE ON PROCEDURE Bookshop.confirmCustomerOrder TO 'Admin';

SHOW GRANTS FOR 'Admin';



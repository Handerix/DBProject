

DROP PROCEDURE IF EXISTS provideBooks;
DROP PROCEDURE IF EXISTS orderBook;
DROP PROCEDURE IF EXISTS orderBookToOrder;
DROP PROCEDURE IF EXISTS getCustomerInformation;
DROP PROCEDURE IF EXISTS getProviderInformation;

DELIMITER $$

CREATE PROCEDURE orderBook(IN login varchar(40), IN passw char(40), OUT result TINYINT(2), IN isbn char(13))
BEGIN
	IF login NOT REGEXP '[a-zA-Z0-9]*' OR passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*' OR (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Customer')
    THEN
		SET result=1;
    ELSE
        SET result=2;
		SET autocommit = 0;
		START TRANSACTION;
		BEGIN
			IF (0) = (SELECT COUNT(*) FROM Books AS B INNER JOIN BookCopies AS BC ON B.ISBN=BC.ISBN WHERE B.ISBN=isbn)
			THEN
				ROLLBACK;
			ELSE            
				SET @d=DATE(NOW());
				INSERT INTO CustomerOrders(customer, datee) VALUE (login, @d);
				SET @id= (SELECT ID FROM CustomerOrders WHERE customer=login AND datee=@d LIMIT 1);
				SET @book= (SELECT ID FROM BookCopies AS BC WHERE BC.ISBN=isbn LIMIT 1);
				if( ISNULL(@id))
                THEN
					ROLLBACK;
                    SET result=2;
				ELSEIF(ISNULL(@book))
                THEN
					ROLLBACK;
					SET result=2;
                ELSE
					INSERT INTO BooksAndOrders(book, customerOrder) VALUE ( @book, @id);
					
					SET result=0;
				END IF;
            END IF;
		END;
		COMMIT;
	END IF;
END
$$

CREATE PROCEDURE provideBooks(IN login varchar(40), IN passw char(40), OUT result TINYINT(2), IN isbn char(13), IN title varchar(40), IN amount int unsigned, IN price int unsigned)
BEGIN
	IF login NOT REGEXP '[a-zA-Z0-9]*' OR passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*' OR (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Provider')
    THEN
		SET result=1;
    ELSE
        SET result=2;
		SET autocommit = 0;
		START TRANSACTION;
		BEGIN
			IF 0 < (SELECT COUNT(*) FROM Books AS B WHERE B.ISBN=isbn)
			THEN
				ROLLBACK;
			ELSE            
				SET @d=DATE(NOW());
				INSERT INTO Books(ISBN, standardPrice, title, releaseDate) VALUE (isbn, price, title, @d);
				if( ISNULL(@id))
                THEN
					ROLLBACK;
                    SET result=2;
                ELSE
					SET @i=amount;
                    WHILE @i>0 
                    DO
						INSERT INTO BookCopies(ISBN) VALUES (isbn, (SELECT address FROM Warehouses ORDER BY rand() LIMIT 1));
						SET @i=@i-1;
                    END WHILE;
					SET result=0;
				END IF;
            END IF;
		END;
		COMMIT;
	END IF;
END
$$

CREATE PROCEDURE orderBookToOrder(IN login varchar(40), IN passw char(40), OUT result TINYINT(2), IN isbn char(13), IN orderID int unsigned)
BEGIN
	IF login NOT REGEXP '[a-zA-Z0-9]*' OR passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*' OR (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Customer')
    THEN
		SET result=1;
    ELSE
        SET result=2;
		SET autocommit = 0;
		START TRANSACTION;
		BEGIN
			IF (0) = (SELECT COUNT(*) FROM Books AS B INNER JOIN BookCopies AS BC ON B.ISBN=BC.ISBN WHERE B.ISBN=isbn)
			THEN
				ROLLBACK;
            ELSE            
				SET @id= (SELECT ID FROM CustomerOrders AS CO WHERE CO.ID=orderID AND CO.customer=login);
				SET @book= (SELECT ID FROM BookCopies AS BC WHERE BC.ISBN=isbn LIMIT 1);
                IF( ISNULL(@id))
                THEN
					ROLLBACK;
                    SET result=2;
				ELSEIF(ISNULL(@book))
                THEN
					ROLLBACK;
					SET result=2;
                ELSE
					INSERT INTO BooksAndOrders(book, customerOrder) VALUE ( @book, @id);
					
					SET result=0;
				END IF;
            END IF;
		END;
		COMMIT;
	END IF;
END
$$


CREATE PROCEDURE getCustomerInformation(IN login varchar(40), IN passw char(40), OUT result TINYINT(2), OUT booksNumber int unsigned,
								OUT orderedCopiesNumber int unsigned, OUT ordersNumber int unsigned, OUT email varchar(40))
BEGIN
	IF login NOT REGEXP '[a-zA-Z0-9]*' OR passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*' OR (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Customer')
    THEN
		SET result=1;
		SET booksNumber= 0;
        SET orderedCopiesNumber= 0;
        SET ordersNumber= 0;
        SET email= 'ERROR';
    ELSE
        SET result=0;
        SET booksNumber= (SELECT COUNT(*) FROM Books);
        SET orderedCopiesNumber= (SELECT COUNT(*) FROM BooksAndOrders INNER JOIN CustomerOrders AS CO WHERE CO.customer=login);
        SET ordersNumber= (SELECT COUNT(*) FROM CustomerOrders AS CO WHERE CO.customer=login);
        SET email= (SELECT email FROM Accounts WHERE typee='Customer' AND Accounts.login=login AND Accounts.passw=passw);
	END IF;
END
$$

CREATE PROCEDURE getProviderInformation(IN login varchar(40), IN passw char(40), OUT result TINYINT(2), OUT booksNumber int unsigned, OUT email varchar(40))
BEGIN
	IF login NOT REGEXP '[a-zA-Z0-9]*' OR passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*' OR (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Provider')
    THEN
		SET result=1;
		SET booksNumber= 0;
        SET email= 'ERROR';
    ELSE
        SET result=0;
        SET booksNumber= (SELECT COUNT(*) FROM Books);
        SET email= (SELECT email FROM Accounts WHERE typee='Provider' AND Accounts.login=login AND Accounts.passw=passw);
	END IF;
END
$$


DELIMITER ;

GRANT EXECUTE ON PROCEDURE orderBook TO 'Customer';
GRANT EXECUTE ON PROCEDURE orderBookToOrder TO 'Customer';
GRANT EXECUTE ON PROCEDURE getCustomerInformation TO 'Customer';
GRANT EXECUTE ON PROCEDURE getProviderInformation TO 'Provider';
GRANT EXECUTE ON PROCEDURE provideBooks TO 'Provider';

SELECT * FROM Accounts WHERE true;



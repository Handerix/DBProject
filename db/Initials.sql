
DELETE FROM BookCopies WHERE TRUE;
DELETE FROM Warehouses WHERE TRUE;
DELETE FROM BooksAndAuthors WHERE TRUE;
DELETE FROM Accounts WHERE TRUE;
DELETE FROM Authors WHERE TRUE;
DELETE FROM Books WHERE TRUE;

DELETE FROM Accounts WHERE typee='Admin';
INSERT INTO Accounts(login, passw, typee) VALUES ('admin1', sha2(concat('sól',CAST('pass' AS char(40)),'sól'), 256), 'Admin'), ('admin2', sha2(concat('sól',CAST('passw' AS char(40)),'sól'), 256), 'Admin');

INSERT INTO Authors(firstname, surname)
SELECT A.imie, B.nazwisko
FROM TempNames AS A CROSS JOIN TempNames AS B
ORDER BY rand()
LIMIT 1000;

DROP PROCEDURE IF EXISTS initBooks;
DROP PROCEDURE IF EXISTS initBooksAndAuthors;
DROP PROCEDURE IF EXISTS initBookCopies;
DROP PROCEDURE IF EXISTS deleteBookCopies;

DELIMITER $$

CREATE PROCEDURE initBooks()
BEGIN
	SET @i=10;
    WHILE @i>0
    DO
		INSERT INTO Books(ISBN, standardPrice, description, title, releaseDate)
		SELECT DISTINCT ((uuid_short()%1000000)*10000000000+floor(rand()*10000000000)+uuid_short())%10000000000000, P.price, D.description, T.title, A.datee
		FROM (SELECT price FROM TempPrices ORDER BY rand() LIMIT 5) AS P 
		CROSS JOIN (SELECT description FROM TempDescriptions ORDER BY rand() LIMIT 8 ) AS D
		CROSS JOIN (SELECT title FROM TempTitles ORDER BY rand() LIMIT 15 ) AS T 
		CROSS JOIN (SELECT datee FROM TempDates ORDER BY rand() LIMIT 15) AS A
		ORDER BY rand()
		LIMIT 5;
        SET @i=@i-1;
	END WHILE;
END
$$

CREATE PROCEDURE initBooksAndAuthors()
BEGIN
	DECLARE done INT DEFAULT FALSE;
    DECLARE isbnn char(13);
	DECLARE csr CURSOR FOR (SELECT ISBN FROM Books);
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	OPEN csr;
		read_loop: LOOP
		
		FETCH csr INTO isbnn;
		IF done THEN
			LEAVE read_loop;
		END IF;
		
		INSERT INTO BooksAndAuthors(ISBN, authorID) 
        SELECT isbnn, ID
        FROM Authors
        ORDER BY rand()
        LIMIT 3;
		
		END LOOP read_loop;
	CLOSE csr;
END
$$

CREATE PROCEDURE initBookCopies()
BEGIN
	DECLARE done INT DEFAULT FALSE;
    DECLARE isbnn char(13);
	DECLARE csr CURSOR FOR (SELECT ISBN FROM Books);
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	OPEN csr;
		read_loop: LOOP
		
		FETCH csr INTO isbnn;
		IF done THEN
			LEAVE read_loop;
		END IF;
		
        SET @i=5;
        WHILE @i>0
        DO
			INSERT INTO BookCopies(ISBN, warehouse) 
			SELECT isbnn, address
			FROM Warehouses
			ORDER BY rand()
			LIMIT 1;
            SET @i=@i-1;
        END WHILE;
		
		END LOOP read_loop;
	CLOSE csr;
END
$$

CREATE PROCEDURE deleteBookCopies()
BEGIN
        SET @i=10000;
        WHILE @i>0
        DO
			DELETE FROM BookCopies;
        END WHILE;
END
$$


DELIMITER ;

CALL initBooks();
CALL initBooks();
CALL initBooks();
CALL initBooks();
CALL initBooksAndAuthors();

INSERT INTO Warehouses(address)
SELECT CONCAT(street, ' ', city) FROM TempAdresses ORDER BY rand() LIMIT 10;

UPDATE Warehouses
SET phone= (SELECT phone FROM TempNumbers ORDER BY rand() LIMIT 1);

CALL initBookCopies();

SELECT * FROM Books;
SELECT * FROM Authors;
SELECT * FROM BooksAndAuthors;
SELECT * FROM BookCopies;


/*DELETE FROM Books WHERE TRUE;
DELETE FROM BooksAndAuthors WHERE TRUE;
DELETE FROM BookCopies WHERE true;
CALL deleteBookCopies();
*/



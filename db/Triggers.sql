
-- triggery: delete CustomerOrder -> delete BooksAndOrders, 

DROP TRIGGER IF EXISTS bookOrderedTr;
DROP TRIGGER IF EXISTS logsDelTr;
DROP TRIGGER IF EXISTS logsInsTr;
DROP TRIGGER IF EXISTS logsUpdTr;
DROP TRIGGER IF EXISTS opinionsTr;
DROP TRIGGER IF EXISTS ordersTr;
DROP TRIGGER IF EXISTS authorsTr;
DROP TRIGGER IF EXISTS booksTr;

DELIMITER $$

CREATE TRIGGER bookOrderedTr AFTER DELETE ON CustomerOrders
FOR EACH ROW
BEGIN
	DELETE FROM BooksAndOrders WHERE customerOrder=OLD.ID;
END
$$

CREATE TRIGGER logsDelTr AFTER DELETE ON ProviderOrders
FOR EACH ROW
BEGIN
	INSERT INTO ProviderOrderLogs(ISBN, performer, amount, inTime, actionn) VALUE (OLD.ISBN, OLD.provider, OLD.amount, NOW(), 'deleted');
END
$$

CREATE TRIGGER logsInsTr AFTER INSERT ON ProviderOrders
FOR EACH ROW
BEGIN
	INSERT INTO ProviderOrderLogs(ISBN, performer, amount, inTime, actionn) VALUE (NEW.ISBN, NEW.provider, NEW.amount, NOW(), 'created');
END
$$

CREATE TRIGGER logsUpdTr AFTER UPDATE ON ProviderOrders
FOR EACH ROW
BEGIN
	INSERT INTO ProviderOrderLogs(ISBN, performer, amount, inTime, actionn) VALUE (OLD.ISBN, OLD.provider, OLD.amount, NOW(), 'deleted');
    INSERT INTO ProviderOrderLogs(ISBN, performer, amount, inTime, actionn) VALUE (NEW.ISBN, NEW.provider, NEW.amount, NOW(), 'created');
END
$$

CREATE TRIGGER opinionsTr AFTER DELETE ON Accounts
FOR EACH ROW
BEGIN
	DELETE FROM Opinions WHERE login=OLD.login;
END
$$

CREATE TRIGGER ordersTr AFTER DELETE ON Accounts
FOR EACH ROW
BEGIN
	IF OLD.typee='Customer'
    THEN
		DELETE FROM CustomerOrders WHERE customer=OLD.login;
	ELSEIF OLD.typee='Provider'
    THEN
		DELETE FROM ProviderOrders WHERE provider=OLD.login;
    END IF;
END
$$

CREATE TRIGGER authorsTr BEFORE DELETE ON Authors
FOR EACH ROW
BEGIN
	DELETE FROM BooksAndAuthors WHERE authorID=OLD.ID;
END
$$

CREATE TRIGGER booksTr BEFORE DELETE ON Books
FOR EACH ROW
BEGIN
	DELETE FROM BooksAndAuthors WHERE ISBN=OLD.ISBN;
    
    DELETE FROM Opinions WHERE ISBN=OLD.ISBN;
END
$$

DELIMITER ;







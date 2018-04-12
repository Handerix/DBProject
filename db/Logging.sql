

DROP PROCEDURE IF EXISTS logAsAdmin;
DROP PROCEDURE IF EXISTS logAsCustomer;
DROP PROCEDURE IF EXISTS logAsProvider;
DROP PROCEDURE IF EXISTS createCustomerAccount;
DROP PROCEDURE IF EXISTS createProviderAccount;

DELIMITER $$

CREATE PROCEDURE logAsAdmin(IN login varchar(40), IN passw char(40), OUT result varchar(40))
BEGIN
	SET result=0;
	IF login NOT REGEXP '[a-zA-Z0-9]*'
    THEN
		SET result=1;
    ELSEIF passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*'
    THEN
		SET result=2;
	ELSEIF (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Admin')
    THEN
		SET result=3;
	END IF;
END
$$

CREATE PROCEDURE logAsCustomer(IN login varchar(40), IN passw char(40), OUT result TINYINT(2))
BEGIN
	SET result=0;
	IF login NOT REGEXP '[a-zA-Z0-9]*'
    THEN
		SET result=1;
    ELSEIF passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*'
    THEN
		SET result=2;
	ELSEIF (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Customer')
    THEN
		SET result=3;
	END IF;
END
$$

CREATE PROCEDURE logAsProvider(IN login varchar(40), IN passw char(40), OUT result TINYINT(2))
BEGIN
	SET result=0;
	IF login NOT REGEXP '[a-zA-Z0-9]*'
    THEN
		SET result=1;
    ELSEIF passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*'
    THEN
		SET result=2;
	ELSEIF (login, sha2(concat('sól',CAST(passw AS char(40)),'sól'), 256)) NOT IN (SELECT A.login, A.passw FROM Accounts AS A WHERE A.typee='Provider')
    THEN
		SET result=3;
	END IF;
END
$$

CREATE PROCEDURE createCustomerAccount(IN login varchar(40), IN passw char(40), OUT result TINYINT(2))
BEGIN
	SET result=0;
	IF login NOT REGEXP '[a-zA-Z0-9]*'
    THEN
		SET result=1;
    ELSEIF passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*'
    THEN
		SET result=2;
	ELSEIF login IN (SELECT A.login FROM Accounts AS A)
    THEN
		SET result=3;
	ELSE
		INSERT INTO Accounts(login, passw, typee) VALUES (login, sha2(concat('sól',passw,'sól'), 256), 'Customer');
	END IF;
END
$$

CREATE PROCEDURE createProviderAccount(IN login varchar(40), IN passw char(40), OUT result TINYINT(2))
BEGIN
	SET result=0;
	IF login NOT REGEXP '[a-zA-Z0-9]*'
    THEN
		SET result=1;
    ELSEIF passw NOT REGEXP '[a-zA-Z0-9!@#$%^&]*'
    THEN
		SET result=2;
	ELSEIF login IN (SELECT A.login FROM Accounts AS A)
    THEN
		SET result=3;
	ELSE
		INSERT INTO Accounts(login, passw, typee) VALUES (login, sha2(concat('sól',passw,'sól'), 256), 'Provider');
	END IF;
END
$$

DELIMITER ;

GRANT EXECUTE ON PROCEDURE Bookshop.logAsAdmin TO 'Admin';
GRANT EXECUTE ON PROCEDURE Bookshop.logAsCustomer TO 'Customer';
GRANT EXECUTE ON PROCEDURE Bookshop.logAsProvider TO 'Provider';
GRANT EXECUTE ON PROCEDURE Bookshop.createCustomerAccount TO 'Customer';
GRANT EXECUTE ON PROCEDURE Bookshop.createProviderAccount TO 'Provider';


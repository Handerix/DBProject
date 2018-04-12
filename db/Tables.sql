


DROP TABLES IF EXISTS	ProviderOrderLogs, ProviderOrders, BooksAndOrders;

DROP TABLES IF EXISTS	CustomerOrders, Bookshops, BookCopies, Opinions, Accounts;

DROP TABLES IF EXISTS 	BooksAndAuthors, Authors, Books, PricePromotions;

CREATE TABLE IF NOT EXISTS PricePromotions(
	ID int unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
    title varchar(15) NOT NULL,
    procentDiscount decimal(4, 2) NOT NULL
    );

CREATE TABLE IF NOT EXISTS Books(
	ISBN char(13) PRIMARY KEY NOT NULL,
    pricePromotions int unsigned NULL,
    standardPrice decimal(10, 2) NOT NULL,
    description text NOT NULL,
    title varchar(50) NOT NULL,
    releaseDate date NOT NULL,
    FOREIGN KEY (pricePromotions) REFERENCES PricePromotions (ID) ON DELETE CASCADE ON UPDATE CASCADE
    );
    

CREATE TABLE IF NOT EXISTS Authors(
	ID int unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
    firstname varchar(30) NOT NULL,
    surname varchar(30) NULL
    );
    
CREATE TABLE IF NOT EXISTS BooksAndAuthors(
    ISBN char(13) NOT NULL,
    authorID int unsigned NOT NULL,
    FOREIGN KEY (ISBN) REFERENCES Books (ISBN) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (authorID) REFERENCES Authors (ID) ON DELETE CASCADE ON UPDATE CASCADE
    );
    
CREATE TABLE IF NOT EXISTS Accounts(
	login varchar(40) PRIMARY KEY NOT NULL,
    passw varchar(60) NOT NULL,
    typee enum('Customer', 'Provider', 'Admin'),
    email varchar(50) NULL
    );
    
CREATE TABLE IF NOT EXISTS Opinions(
	ISBN char(13) NOT NULL,
    login varchar(30) NOT NULL,
    textt text NOT NULL,
    inTime datetime NOT NULL,
    FOREIGN KEY (ISBN) REFERENCES Books (ISBN) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (login) REFERENCES Accounts (login) ON DELETE CASCADE ON UPDATE CASCADE
    );
    
CREATE TABLE IF NOT EXISTS BookCopies(
	ID bigint unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
    ISBN char(13) NOT NULL,
    bookshop varchar(80) NULL
    );
    
CREATE TABLE IF NOT EXISTS Bookshops(
	address varchar(80) PRIMARY KEY NOT NULL,
    phone varchar(20) NULL,
    hours varchar(70) NULL
    );
    
CREATE TABLE IF NOT EXISTS CustomerOrders(
	ID int unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
    customer varchar(40) NOT NULL,
    datee date NULL,
    FOREIGN KEY (customer) REFERENCES Accounts (login) ON DELETE CASCADE ON UPDATE CASCADE
    );
    
CREATE TABLE IF NOT EXISTS BooksAndOrders(
	ID int unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
    book bigint unsigned NOT NULL,
    customerOrder int unsigned NOT NULL,
    FOREIGN KEY (book) REFERENCES BookCopies (ID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (customerOrder) REFERENCES CustomerOrders (ID) ON DELETE CASCADE ON UPDATE CASCADE
	);
    
CREATE TABLE IF NOT EXISTS ProviderOrders(
	ID int unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
    ISBN char(13) NOT NULL,
    provider varchar(40) NOT NULL,
    shop varchar(80) NOT NULL,
    amount int,
    FOREIGN KEY (ISBN) REFERENCES Books (ISBN) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (provider) REFERENCES Accounts (login) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (shop) REFERENCES Bookshops (address) ON DELETE CASCADE ON UPDATE CASCADE
    );
    
CREATE TABLE IF NOT EXISTS ProviderOrderLogs(
	ISBN char(13)  NOT NULL,
    performer varchar(40) NOT NULL,
    amount int NOT NULL,
    inTime datetime NOT NULL,
    actionn enum('created', 'accepted'),
    FOREIGN KEY (ISBN) REFERENCES Books (ISBN) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (performer) REFERENCES Accounts (login) ON DELETE CASCADE ON UPDATE CASCADE
    );
    
CREATE INDEX ISBN_INDEX USING BTREE ON Books(ISBN);

CREATE INDEX AUTHORS_INDEX USING BTREE ON BooksAndAuthors(authorID);

CREATE INDEX PRICE_INDEX USING BTREE ON Books(standardPrice);

CREATE INDEX COMMENTS_INDEX USING BTREE ON Opinions(ISBN);














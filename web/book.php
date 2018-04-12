<?php
    session_start();
    $login=$_SESSION['login'];
    $password=$_SESSION['password'];
    $id=$_SESSION['id'];
    if($login==null || $id==null || $password==null || $id!='c' )
    {
        header("Location: false.php");
        die();
        exit();
    }

    require_once 'databaseInfo.php';
    $conn = new mysqli($hn, $un, $pw, $db);
    if ($conn->connect_error) die("MySQL connection: (" . $conn->connect_errno . ") " . $conn->connect_error);

    if(!isset($_GET['ISBN']) || $_GET['ISBN']==null)
    {
        die('ISBN not set');
    }
    $isbn=$_GET['ISBN'];

    $result= $conn->query("SELECT DISTINCT B.standardPrice, B.title, B.description, B.releaseDate, PP.procentDiscount, GROUP_CONCAT(DISTINCT CONCAT(A.firstname, ' ', A.surname) SEPARATOR ';') AS s, COUNT(BC.ID) AS c ".
                          "FROM Authors AS A INNER JOIN BooksAndAuthors AS BAA ON BAA.authorID=A.ID ".
                          "INNER JOIN Books AS B ON B.ISBN=BAA.ISBN INNER JOIN BookCopies AS BC ON BC.ISBN=B.ISBN ".
                          "LEFT JOIN BooksAndOrders AS BAO ON BC.ID=BAO.book LEFT JOIN PricePromotions AS PP ON B.pricePromotions=PP.ID ".
                          "WHERE B.ISBN= '".$isbn."' ".
                          "GROUP BY B.ISBN, B.standardPrice, B.title, PP.procentDiscount ");

    if ($conn->connect_error || !$result)
    {
        die("MySQL: (" . $conn->connect_errno . ") " . $conn->error . " " . $result);
    }
    $result->data_seek(0);
    $row= $result->fetch_array(MYSQLI_NUM);
    $price=$row[0];
    $title=$row[1];
    $desc=$row[2];
    $releaseDate=$row[3];
    $procentDiscount=$row[4];
    $authors=$row[5];
    $copies=$row[6];

    if($copies==null || $copies<1)
    {
        die("Error: No copies ");
    }

    if($procentDiscount==null)
        $procentDiscount='None';

echo <<<_END
    <html>
        <head>
            <meta charset="UTF-8">
            <title>Transactions</title>
        </head>
        <body>
            <h2>  Logged as: $login   </h2> <br>
            <a href="logout.php">Log out</a>
            <h4> Available books browser: </h4>
            <a href="booksSearch.php">Search for books</a>
            <h1> Book: </h1>
            <form action='orderBook.php' method='get'>
                ISBN: $isbn <br>
                Title: $title <br>
                Authors: $authors <br>
                Price: $price <br>
                Release Date: $releaseDate <br>
                Percent Discount: $procentDiscount <br>
                Copies: $copies <br>
                <input type="submit" value="Order">
                <input type='hidden' name='ISBN' value=$isbn>
                <input type='hidden' name='title' value=$isbn>
            </form> <br>
            <form action='orderBookToOrder.php' method='get'>
                <input type="number" name="orderID">
                <input type="submit" value="Add to existing Order">
                <input type='hidden' name='ISBN' value=$isbn>
                <input type='hidden' name='title' value=$isbn>
            </form> <br>
            <h2> Description: </h2> <br>
            <p5> $desc </p5> <br>
_END;


    $result= $conn->query("SELECT DISTINCT login, textt, inTime ".
                          "FROM Opinions AS O ".
                          "WHERE O.ISBN=".$isbn." ");

    if ($conn->connect_error || !$result)
    {
        die("MySQL: (" . $conn->connect_errno . ") " . $conn->error . " " . $result);
    }
    $rows= $result->num_rows;

    echo '<h2> Opinions: <h2/> <br>';
    echo '<table><tr><th>User</th><th>Desc</th><th>Time</th></tr>';
    for($j=0; $j<$rows; ++$j)
    {
        $result->data_seek($j);
        $row= $result->fetch_array(MYSQLI_NUM);
        echo "<tr>";
        echo "<td> $row[0]</td>";
        echo "<td> $row[1]</td>";
        echo "<td> $row[2]</td>";
        echo "</tr>";
    }
    echo "</table> <br> <br>";



echo <<<_END
        </body>
    </html>
_END;

$result->close();
$conn->close();
?>




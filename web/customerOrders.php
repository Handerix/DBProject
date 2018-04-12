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




    $result= $conn->query("SELECT DISTINCT BAO.customerOrder, B.ISBN, B.standardPrice, B.title, PP.procentDiscount, GROUP_CONCAT(DISTINCT CONCAT(A.firstname, ' ', A.surname) SEPARATOR ';') AS s, COUNT(BC.ID) AS c ".
                               " FROM Authors AS A INNER JOIN BooksAndAuthors AS BAA ON BAA.authorID=A.ID ".
                               " INNER JOIN Books AS B ON B.ISBN=BAA.ISBN INNER JOIN BookCopies AS BC ON BC.ISBN=B.ISBN ".
                               " INNER JOIN BooksAndOrders AS BAO ON BC.ID=BAO.book LEFT JOIN PricePromotions AS PP ON B.pricePromotions=PP.ID INNER JOIN CustomerOrders AS CO ON CO.ID=BAO.customerOrder ".
                               " WHERE CO.customer='".$login."' ".
                               " GROUP BY B.ISBN, B.standardPrice, B.title, PP.procentDiscount ".
                               " ORDER BY BAO.customerOrder ");

    if ($conn->connect_error || !$result)
    {
        die("MySQL: (" . $conn->connect_errno . ") " . $conn->error . " " . $result);
    }
    $rows= $result->num_rows;


    echo <<<_END
    <html>
        <head>
            <meta charset="UTF-8">
            <title>Transactions</title>
        </head>
        <body>
            <h2>  Logged as: $login   <h2/> <br>
            <a href="logout.php">Log out</a>
            <h4> Available books browser: <h4/>
            <a href="booksSearch.php">Search for books</a>
_END;

    echo '<h3> Number of results: '.$rows.' <h3/>';
    echo '<table><tr><th>Order</th><th>ISBN</th><th>Standard Price</th><th>Title</th><th>Percent Discount</th><th>Authors</th><th>Number of Copies</th></tr>';
    for($j=0; $j<$rows; ++$j)
    {
        $result->data_seek($j);
        $row= $result->fetch_array(MYSQLI_NUM);
        echo "<tr>";
        for($k=0; $k<7; ++$k)
        {
            if($row[$k]==null)
                echo "<td>None</td>";
            else
                echo "<td> $row[$k]</td>";
        }
        echo "</tr>";
    }
    echo "</table>";

    echo <<<_END
        </body>
    </html>
_END;

$result->close();
$conn->close();
?>



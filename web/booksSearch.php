<?php
    session_start();
    $login=$_SESSION['login'];
    $password=$_SESSION['password'];
    $id=$_SESSION['id'];
    if($login==null || $id==null || $password==null || ($id!='c' && $id!='p') )
    {
        header("Location: false.php");
        die();
        exit();
    }

    require_once 'databaseInfo.php';
    if($id=='c')
    {
        $conn = new mysqli($hn, $un, $pw, $db);
    }
    else
    {
        $conn = new mysqli($hn, $un2, $pw2, $db);
    }
    if ($conn->connect_error) die("MySQL connection: (" . $conn->connect_errno . ") " . $conn->connect_error);

    $amount=30;
    if(!isset($_GET['start']))
    {
        $start=0;
    }
    else
    {
        $start=$_GET['start'];
    }
    if(!isset($_GET['minPrice']) || $_GET['minPrice']==null)
    {
        $minPrice=0;
    }
    else
    {
        $minPrice=$_GET['minPrice'];
    }
    if(!isset($_GET['maxPrice']) || $_GET['maxPrice']==null)
    {
        $maxPrice=100000;
    }
    else
    {
        $maxPrice=$_GET['maxPrice'];
    }
    if(!isset($_GET['titleLike']) || $_GET['titleLike']==null)
    {
        $titleLike="B.title";
    }
    else
    {
        $titleLike="'".$_GET['titleLike']."'";
    }
    if(!isset($_GET['authorLike']) || $_GET['authorLike']==null)
    {
        $authorLike="s";
    }
    else
    {
        $authorLike="'".$_GET['authorLike']."'";
    }



    $result= $conn->query("SELECT DISTINCT B.ISBN, B.standardPrice, B.title, PP.procentDiscount, GROUP_CONCAT(DISTINCT CONCAT(A.firstname, ' ', A.surname) SEPARATOR ';') AS s, COUNT(BC.ID) AS c ".
                               " FROM Authors AS A INNER JOIN BooksAndAuthors AS BAA ON BAA.authorID=A.ID ".
                               " INNER JOIN Books AS B ON B.ISBN=BAA.ISBN INNER JOIN BookCopies AS BC ON BC.ISBN=B.ISBN ".
                               " LEFT JOIN BooksAndOrders AS BAO ON BC.ID=BAO.book LEFT JOIN PricePromotions AS PP ON B.pricePromotions=PP.ID ".
                               " GROUP BY B.ISBN, B.standardPrice, B.title, PP.procentDiscount ".
                               " HAVING c>0 ".
                               " AND B.standardPrice BETWEEN ".$minPrice." AND ".$maxPrice." ".
                               " AND B.title LIKE CONCAT('%', ".$titleLike.", '%') ".
                               " AND s LIKE CONCAT('%', ".$authorLike.", '%') ".
                               " ORDER BY B.standardPrice ".
                               " LIMIT ".$start.", ".$amount );

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
            <h1> Search: </h1>
            <form action='booksSearch.php' method='get'>
                Title: <input type="text" name="titleLike"><br>
                Author: <input type="text" name="authorsLike"><br>
                Min Price: <input type="number" name="minPrice"><br>
                Max Price: <input type="number" name="maxPrice"><br>
                <input type="submit" value="Search">
            </form> <br>
_END;

    echo '<h3> Number of results on Page: '.$rows.' <h3/>';
    echo '<table><tr><th>ISBN</th><th>Standard Price</th><th>Title</th><th>Percent Discount</th><th>Authors</th><th>Number of Copies</th></tr>';
    for($j=0; $j<$rows; ++$j)
    {
        $result->data_seek($j);
        $row= $result->fetch_array(MYSQLI_NUM);
        echo "<tr>";
        for($k=0; $k<6; ++$k)
        {
            if($row[$k]==null)
                echo "<td>None</td>";
            else
                echo "<td> $row[$k]</td>";
        }
        echo "<td> <form action='book.php' method='get'> <input type='submit' value='Show'>";
        echo "<input type='hidden' name='ISBN' value='$row[0]'>";
        echo "</form>";
        echo "</td>";
        echo "</tr>";
    }
    echo "</table>";

    if($start>0)
    {
        $back=$start-$amount;
            if($back<0)
                $back=0;
        echo "<form action='booksSearch.php' method='get'> <input type='submit' value='Back'>";
        echo "<input type='hidden' name='start' value='$back'>";

        if(isset($_GET['minPrice']))
        {
            $g=$_GET['minPrice'];
            echo "<input type='hidden' name='minPrice' value='$g'>";
        }

        if(isset($_GET['maxPrice']))
        {
            $g=$_GET['maxPrice'];
            echo "<input type='hidden' name='maxPrice' value='$g'>";
        }

        if(isset($_GET['titleLike']))
        {
            $g=$_GET['titleLike'];
            echo "<input type='hidden' name='titleLike' value='$g'>";
        }

        if(isset($_GET['authorLike']))
        {
            $g=$_GET['authorLike'];
            echo "<input type='hidden' name='authorLike' value='$g'>";
        }

        echo "</form>";
    }

    echo "<form action='booksSearch.php' method='get'> <input type='submit' value='Next'>";
    $next=$start+$amount;
    echo "<input type='hidden' name='start' value='$next'>";

    if(isset($_GET['minPrice']))
    {
        $g=$_GET['minPrice'];
        echo "<input type='hidden' name='minPrice' value='$g'>";
    }

    if(isset($_GET['maxPrice']))
    {
        $g=$_GET['maxPrice'];
        echo "<input type='hidden' name='maxPrice' value='$g'>";
    }

    if(isset($_GET['titleLike']))
    {
        $g=$_GET['titleLike'];
        echo "<input type='hidden' name='titleLike' value='$g'>";
    }

    if(isset($_GET['authorLike']))
    {
        $g=$_GET['authorLike'];
        echo "<input type='hidden' name='authorLike' value='$g'>";
    }

    echo "</form>";

    echo <<<_END
        </body>
    </html>
_END;

$result->close();
$conn->close();
?>
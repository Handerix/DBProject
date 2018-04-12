<?php
    session_start();
    $login=$_SESSION['login'];
    $password=$_SESSION['password'];
    $id=$_SESSION['id'];
    if($login==null || $id==null || $password==null || $id!='c')
    {
        header("Location: false.php");
        die();
        exit();
    }

    require_once 'databaseInfo.php';
    $conn = new mysqli($hn, $un, $pw, $db);
    if ($conn->connect_error) die("Mysqli error: ".$conn->connect_error);

    $statement= $conn->prepare("Call getCustomerInformation( ?, ?, @res, @booksNumb, @ordCopies, @orders, @email)");
    $statement->bind_param('ss', $login, $password);
    $statement->execute();
    if ($conn->connect_error)
    {
        die("MySQL: (" . $conn->connect_errno . ") " . $conn->connect_error);
    }
    $statement->close();

    $select = $conn->query('SELECT @res AS r, @booksNumb, @ordCopies, @orders, @email');
    if(!$select)
    {
        die("MySQL: (" . $conn->errno . ") " . $conn->error);
    }
    $result = $select->fetch_assoc();
    $res = $result['r'];
    if($res==null)
        die("result is null");

    if($res!=0)
    {
        header("Location: false.php");
        die();
        exit();
    }

    echo <<<_END
    <html>
        <head>
            <meta charset="UTF-8">
            <title>Transactions</title>
        </head>
        <body>
            <h2>  Logged as Customer: $login   <h2/> <br>
            <a href="logout.php">Log out</a>
            <h4> Available books browser: <h4/>
            <a href="booksSearch.php">Search for books</a>
            <h4> Your Orders: <h4/>
            <a href="customerOrders.php">Show orders</a>
_END;

    echo '<h5> Number of all books: '.$result['@booksNumb'].' <h5/>';
    echo '<h5> Number of your ordered books: '.$result['@ordCopies'].' <h5/>';
    echo '<h5> Number of your Orders: '.$result['@orders'].' <h5/>';
    echo '<h5> Email: '.$result['@email'].' <h5/>';

    echo <<<_END
        </body>
    </html>
_END;

$conn->close();
?>
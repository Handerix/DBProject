<?php
    session_start();
    $login=$_SESSION['login'];
    $password=$_SESSION['password'];
    $id=$_SESSION['id'];
    if($login==null || $id==null || $password==null || $id!='p')
    {
        header("Location: false.php");
        die();
        exit();
    }

    require_once 'databaseInfo.php';
    $conn = new mysqli($hn, $un2, $pw2, $db);
    if ($conn->error) die($conn->error);

    $statement= $conn->prepare("Call getProviderInformation( ?, ?, @res, @booksNumb, @email)");
    $statement->bind_param('ss', $login, $password);
    $statement->execute();
    if ($conn->connect_error)
    {
        die("MySQL: (" . $conn->connect_errno . ") " . $conn->connect_error);
    }
    $statement->close();

    $select = $conn->query('SELECT @res AS r, @booksNumb, @email');
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
            <h4> Inform about providing: <h4/>
            <a href="providing.php">Inform</a>
            <h4> Your Orders: <h4/>
            <a href="providerOrders.php">Show orders</a>
_END;

    echo '<h5> Number of all books: '.$result['@booksNumb'].' <h5/>';
    echo '<h5> Email: '.$result['@email'].' <h5/>';

    echo <<<_END
        </body>
    </html>
_END;

$conn->close();
?>
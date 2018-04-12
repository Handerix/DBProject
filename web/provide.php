<?php
    session_start();
    $login=$_SESSION['login'];
    $password=$_SESSION['password'];
    $id=$_SESSION['id'];
    if($login==null || $id==null || $password==null || $id!='p' )
    {
        header("Location: false.php");
        die();
        exit();
    }

    require_once 'databaseInfo.php';
    $conn = new mysqli($hn, $un2, $pw2, $db);
    if ($conn->connect_error) die("MySQL connection: (" . $conn->connect_errno . ") " . $conn->connect_error);

    if(!isset($_GET['ISBN']) || $_GET['ISBN']==null)
    {
        die('ISBN not set');
    }
    $isbn=$_GET['ISBN'];

    if(!isset($_GET['title']) || $_GET['title']==null)
    {
        die('title not set');
    }
    $title=$_GET['title'];


    if(!isset($_GET['price']) || $_GET['price']==null)
    {
        die('price not set');
    }
    $price=$_GET['price'];

    if(!isset($_GET['amount']) || $_GET['amount']==null)
    {
        die('Amount not set');
    }
    $amount=$_GET['amount'];


    $statement= $conn->prepare("Call provideBooks( ?, ?, @res, ?, ?, ?, ?)");
    $statement->bind_param('ssssii', $login, $password, $isbn, $title, $amount, $price);
    if ($conn->error)
    {
        die("MySQL: (" . $conn->errno . ") " . $conn->error);
    }
    $statement->execute();
    if ($conn->error)
    {
        die("MySQL: (" . $conn->errno . ") " . $conn->error);
    }
    $statement->close();
    $select = $conn->query('SELECT @res AS r');
    $result = $select->fetch_assoc();
    $res = $result['r'];
    if($res==null)
        die("result is null");

    if($res==1)
    {
        header("Location: false.php");
        die();
        exit();
    }
    else if($res==2)
    {
        die("Transaction failed!");
    }


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
            <h1> Book Ordered!: </h1>
            <p3>
                ISBN: $isbn <br>
                Title: $title <br>
                Amount: $amount <br>
            </p3>
            <h3> Go to Main Page </h3> <br>
            <a href="customer.php">Main Page</a>
        </body>
    </html>
_END;

$select->close();
$conn->close();
?>

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



echo <<<_END
    <html>
        <head>
            <meta charset="UTF-8">
            <title>Transactions</title>
        </head>
        <body>
            <h2>  Logged as: $login   </h2> <br>
            <a href="logout.php">Log out</a>
            <h1> Providing Books: </h1>
            <form action='provide.php' method='get'>
                ISBN: <input type="text" name="ISBN"> <br>
                Title: <input type="text" name="title"> <br>
                Price: <input type="number" name="price"> <br>
                Description: <input type="text" name="desc"> <br>
                Amount: <input type="number" name="amount"> <br>
                <input type="submit" value="Inform">
            </form> <br>
            <h3> Go to Main Page </h3> <br>
            <a href="provider.php">Main Page</a>
        </body>
    </html>
_END;

$conn->close();
?>


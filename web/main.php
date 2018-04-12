<?php
    session_start();
    $login=$_SESSION['login'];
    $id=$_SESSION['id'];
    if($login==null || $id==null || $id!='admin')
    {
        header("Location: false.php");
        die();
        exit();
    }

    require_once 'databaseInfo.php';
    $conn = new mysqli($hn, $un, $pw, $db);
    if ($conn->connect_error) die($conn->connect_error);

    $statement= $conn->prepare( "SELECT T.timee, T.recipientANumber, T.amount, T.recipientNames, U.login ".
                                "FROM Transactions AS T INNER JOIN Users AS U ON T.userID=U.ID ".
                                ' WHERE state =  "unrealized" ');
    echo mysqli_error($conn);
    $statement->execute();
    $result=$statement->get_result();
    $statement->close();


    echo <<<_END
        <html>
            <head>
                <meta charset="UTF-8">
                <title>Transactions</title>
            </head>
            <body>
                <h2>  Logged as Admin: $login   <h2/> <br>
                <a href="logout.php">Log out</a>
                <h3> Transactions: <h3/>
                <table id="TransTable">
_END;

        while($row = $result->fetch_assoc())
        {
            echo '<tr> <th id="Time"> '.$row['timee'].'</th> <th id="NR"> NR: '.$row['recipientANumber'].'</th>';
            echo '<th> User: '.$row['login'].'</th> <th> Amount:  '.$row['amount'].'PLN </th> <th>';
            echo '<form method="get" action="submitTrans.php"> <input type="submit" value="submit"> <br> <input type="hidden" name="time" value="'.$row['timee'].'"><input type="hidden" name="user" value="'.$row['login'].'"></form>';
            echo '</th> </tr> ';
        }

        echo <<<_END
                </table>
            </body>
        </html>
_END;

    $conn->close();
?>
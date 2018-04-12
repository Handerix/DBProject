<?php
    require_once 'databaseInfo.php';

    if (!isset($_POST['login']) || !isset($_POST['password']) || !isset($_POST["actionType"]) )
    {
        die("Not Logged Or Wrong Data");
    }

    if($_POST['actionType']=="R" || $_POST['actionType']=="L")
    {
        $conn = new mysqli($hn, $un, $pw, $db);
        if ($conn->connect_error) die($conn->connect_error);
    }
    else
    {
        $conn = new mysqli($hn, $un2, $pw2, $db);
        if ($conn->connect_error) die($conn->connect_error);
    }

    $salt="sol";
    $login= $_POST['login'];
    $password= hash('sha256', $salt . $_POST['password'] . $salt);
    $password= substr($password, 0, 39);

    if($_POST['actionType']=="R") // rejestracja customera
    {
        $statement= $conn->prepare("Call createCustomerAccount( ?, ?, @res)");
        $statement->bind_param('ss', $login, $password);
        if ($conn->error)
        {
            die("MySQL: (" . $conn->errno . ") " . $conn->error);
        }
        $statement->execute();
        $statement->close();
        if ($conn->errno)
        {
            die("MySQL: (" . $conn->errno . ") " . $conn->error);
        }

        $select = $conn->query('SELECT @res AS r');
        if(!$select)
        {
            die("Table creation failed: (" . $conn->errno . ") " . $conn->error);
        }
        $result = $select->fetch_assoc();
        $res = $result['r'];
        if($res==null)
                die("result is null");


        if($res==1)
        {
            die("Bad login");
        }
        else if($res==2)
        {
            die("Bad password");
        }
        else if($res==3)
        {
            die("Login exists");
        }

        $_POST['actionType']="L";
    }
    else if($_POST['actionType']=="r") // rejestracja providera
    {
        $statement= $conn->prepare("Call createProviderAccount( ?, ?, @res)");
        $statement->bind_param('ss', $login, $password);
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
        if ($conn->errno)
        {
            die("MySQL: (" . $conn->errno . ") " . $conn->error);
        }

        $select = $conn->query('SELECT @res AS r');
        if(!$select)
        {
            die("Table creation failed: (" . $conn->errno . ") " . $conn->error);
        }
        $result = $select->fetch_assoc();
        $res = $result['r'];
        if($res==null)
            die("result is null");


        if($res==1)
        {
            die("Bad login");
        }
        else if($res==2)
        {
            die("Bad password");
        }
        else if($res==3)
        {
            die("Login exists");
        }

        $_POST['actionType']="l";
    }

    if($_POST['actionType']=="L") // logowanie customera
    {
        $statement= $conn->prepare("Call logAsCustomer( ?, ?, @res)");
        $statement->bind_param('ss', $login, $password);
        if ($conn->error)
        {
            die("Failed to connect to MySQL: (" . $conn->errno . ") " . $conn->error);
        }
        $statement->execute();
        if ($conn->errno)
        {
            die("MySQL: (" . $conn->errno . ") " . $conn->error);
        }
        $statement->close();

        $select = $conn->query('SELECT @res AS r');
        if(!$select)
        {
            die("Table creation failed: (" . $conn->errno . ") " . $conn->error);
        }
        $result = $select->fetch_assoc();
        $res = $result['r'];
        if($res==null)
                die("result is null");


        if($res==1)
        {
            die("Bad login");
        }
        else if($res==2)
        {
            die("Bad password");
        }
        else if($res==3)
        {
            die("Account doesn't exists");
        }
        $conn->close();
        session_start();
        $_SESSION['res']=$res;
        $_SESSION['login']=$login;
        $_SESSION['password']=$password;
        $_SESSION['id']='c';
        header("Location: customer.php");
    }
    else // logowanie providera
    {
        $statement= $conn->prepare("Call logAsProvider( ?, ?, @res)");
        $statement->bind_param('ss', $login, $password);
        if ($conn->error)
        {
            die("Failed to connect to MySQL: (" . $conn->errno . ") " . $conn->error);
        }
        $statement->execute();
        if ($conn->errno)
        {
            die("MySQL: (" . $conn->errno . ") " . $conn->error);
        }
        $statement->close();

        $select = $conn->query('SELECT @res AS r');
        if(!$select || $conn->error)
        {
            die("Table creation failed: (" . $conn->errno . ") " . $conn->error);
        }
        $result = $select->fetch_assoc();
        $res = $result['r'];
        if($res==null)
            die("result is null");


        if($res==1)
        {
            die("Bad login");
        }
        else if($res==2)
        {
            die("Bad password");
        }
        else if($res==3)
        {
            die("Account doesn't exists");
        }
        $conn->close();
        session_start();
        $_SESSION['res']=$res;
        $_SESSION['login']=$login;
        $_SESSION['password']=$password;
        $_SESSION['id']='p';
        header("Location: provider.php");
    }
    die();
    exit();
?>
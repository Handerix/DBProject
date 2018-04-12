<?php
echo <<<_END
<html>
    <head>
        <meta charset="UTF-8">
        <title>Logging</title>
    </head>
    <body>
        <form method="post" action="login.php">
            Customer Logging: Login and Password <br>
            <input type="text" name="login" required> <br>
            <input type="text" name="password" required> <br>
            <input type="submit" value="log in"> <br>
            <input type="hidden" name="actionType" value="L">
        </form>

        <form method="post" action="login.php">
            Customer Registration: Login and Password <br>
            <input type="text" name="login" required> <br>
            <input type="text" name="password" required> <br>
            <input type="submit" value="log in"> <br>
            <input type="hidden" name="actionType" value="R">
        </form>

        <form method='post' action="login.php">
             Provider Logging: Login and Password <br>
            <input type='text' name='login' required> <br>
            <input type='text' name='password' required> <br>
            <input type='submit' value='log in'> <br>
            <input type='hidden' name='actionType' value="l">
        </form>

        <form method='post' action="login.php">
             Provider Registration: Login and Password <br>
            <input type='text' name='login' required> <br>
            <input type='text' name='password' required> <br>
            <input type='submit' value='log in'> <br>
            <input type='hidden' name='actionType' value="r">
        </form>
    </body>
</html>
_END
?>

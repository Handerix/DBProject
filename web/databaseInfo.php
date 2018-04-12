<?php
    $hn = 'localhost';
    $db = 'Bookshop';
    $pw = '123';
    $pw2 = '456';
    $un = 'Customer';
    $un2 = 'Provider';

    function sanitizeString($var)
    {
        $var = stripslashes($var);
        $var = strip_tags($var);
        $var = htmlentities($var);
        return $var;
    };

?>
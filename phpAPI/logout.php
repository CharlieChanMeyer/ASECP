<?php

if (!empty($_POST['email']) && !empty($_POST['apiKey'])) {
    $con = mysqli_connect("thesis-database.cmdyp0pvnymo.ap-northeast-3.rds.amazonaws.com:3306", "root", "J58myKHNAAihjSX", "asecp");
    $email = $_POST["email"];
    $apiKey = $_POST["apiKey"];
    if ($con) {
        $sql = "SELECT * FROM users WHERE email = '$email' AND api_key = '$apiKey'";
        $res = mysqli_query($con, $sql);
        if (mysqli_num_rows($res) != 0) {
            $row = mysqli_fetch_assoc($res);
            $sqlUpdate = "UPDATE users SET api_key = '' WHERE email = '$email'";
            if (mysqli_query($con, $sqlUpdate)) {
                echo "success";
            } else echo "logout failed";
        } else echo "Unauthorised to access";
    } else echo "Database connection failed";
} else echo "All fields are required";
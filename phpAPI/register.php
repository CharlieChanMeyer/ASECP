<?php
if (!empty($_POST['email']) && !empty($_POST['password'])) {
    $con = mysqli_connect("thesis-database.cmdyp0pvnymo.ap-northeast-3.rds.amazonaws.com:3306", "root", "J58myKHNAAihjSX", "asecp");
    $email = $_POST["email"];
    $password = password_hash($_POST["password"], PASSWORD_DEFAULT);
    if ($con) {
        $sql = "INSERT INTO users (email, password) VALUES ('$email', '$password')";
        if (mysqli_query($con,$sql)) {
            echo "success";
        } else echo "failed";
    } else echo "Database connection failed!";
} else echo "All fields required!";
<?php
$_POST['email'] = "root";
$_POST['password'] = "1234";
if (!empty($_POST['email']) && !empty($_POST['password'])) {
    $con = mysqli_connect("thesis-database.cmdyp0pvnymo.ap-northeast-3.rds.amazonaws.com:3306", "root", "J58myKHNAAihjSX", "asecp");
    $email = $_POST["email"];
    $password = $_POST["password"];
    $result = array();
    if ($con) {
        $sql = "SELECT * from users WHERE email = '$email'";
        $res = mysqli_query($con, $sql);
        if (mysqli_num_rows($res) != 0) {
            $row = mysqli_fetch_assoc($res);
            if ($email == $row['email'] && password_verify($password, $row['password'])) {
                try {
                    $apiKey = bin2hex(random_bytes(23));
                } catch (Exception $e) {
                    $apiKey = bin2hex(uniqid($email, true));
                }
                $sqlUpdate = "UPDATE users SET api_key = '$apiKey' where email = '$email'";
                if (mysqli_query($con,$sqlUpdate)) {
                    $result = array(
                        "status" => "success",
                        "message" => "Login successful",
                        "id" => $row['id'],
                        "name" => $row['name'],
                        "email" => $row["email"],
                        "apiKey" => "$apiKey"
                    );
                } else $result = array("status" => "failed", "message" => "Login failed try again");
            } else $result = array("status" => "failed", "message" => "Retry with corrrect email and password");
        } else $result = array("status" => "failed", "message" => "Retry with corrrect email and password");
    } else $result = array("status" => "failed", "message" => "Database connection failed");
} else $result = array("status" => "failed", "message" => "All fields are required");

echo json_encode($result, JSON_PRETTY_PRINT); 
<?php
$con = mysqli_connect("thesis-database.cmdyp0pvnymo.ap-northeast-3.rds.amazonaws.com:3306", "antoine", "CMI7EU7T1erahBm3", "asecp");

if (!empty($_POST['id'])) {
    $id = $_POST['id'];
    $result = array();
    if ($con) {
        $sql = "SELECT * FROM restaurants WHERE id = '$id'";
        $res = mysqli_query($con, $sql);
        if (mysqli_num_rows($res) != 0) {
            $row = mysqli_fetch_assoc($res);
            $result = array(
                "status" => "success",
                "message" => "Data fetched successfully",
                "name" => $row['name'],
                "description" => $row['description'],
                "grade" => $row['grade'],
                "photo" => $row['photo'],
                "GPS" => $row['GPS']
            );
        } else
            $result = array("status" => "failed", "message" => "Non-existence");
    } else
        $result = array("status" => "failed", "message" => "Database connection failed");
} else 
    $result = array("status" => "failed", "message" => "All fields required!"); 

echo json_encode($result, JSON_PRETTY_PRINT);
?>

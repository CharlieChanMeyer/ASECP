<?php
$con = mysqli_connect("thesis-database.cmdyp0pvnymo.ap-northeast-3.rds.amazonaws.com:3306", "root", "J58myKHNAAihjSX", "asecp");
$result = "";
if ($con) {
    $sql = "SELECT id,name,GPS FROM restaurants";
    $res = mysqli_query($con, $sql);
    if (mysqli_num_rows($res) != 0) {
        $result = "success";
        while ($row = mysqli_fetch_array($res, MYSQLI_ASSOC))
        {
            $result .= "|".$row["id"]."/".$row["name"]."/".$row["GPS"];
        }
        
    } else $result = "status|failed|message|No restaurants found";
} else $result = "status|failed|message|Database connection failed";

echo $result; 
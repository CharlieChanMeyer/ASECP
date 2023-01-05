<?php

function bidon(int $a, int $b) {
    
    return($a + $b);
}

function degreesToRadians(float $degrees) {
    return ($degrees * pi()/180);
}

function distanceInKmBetweenEarthCoordinates(float $lat1, float $lon1, float $lat2, float $lon2) {

    $earthRadiusKm = 6371;

    $dLat = degreesToRadians($lat2-$lat1);
    $dLon = degreesToRadians($lon2-$lon1);
  
    $lat1 = degreesToRadians($lat1);
    $lat2 = degreesToRadians($lat2);

    $a = sin($dLat/2) * sin($dLat/2) + sin($dLon/2) * sin($dLon/2) * cos($lat1) * cos($lat2); 
    $c = 2 * atan2(sqrt($a), sqrt(1-$a)); 

    return ($earthRadiusKm * $c);
}

    

if (!empty($_POST["pos"])){
    $con = mysqli_connect("thesis-database.cmdyp0pvnymo.ap-northeast-3.rds.amazonaws.com:3306", "antoine", "CMI7EU7T1erahBm3", "asecp");
    $pos = $_POST["pos"];
    $pos = explode(",", $pos);
    $result = array();
    $close_restaurants = array();

    if ($con) {
        $sql1 = "SELECT * FROM restaurants";
        $res1 =  mysqli_query($con, $sql1);
        if ($res1 != 0){
            $size = mysqli_num_rows($res1);
            for ($x = 0; $x < $size ; $x++) {
                if (distanceInKmBetweenEarthCoordinates($pos[0], $pos[1], $res1[$x][5][0], $res1[$x][5][1]) < 2) {
                    array_push($close_restaurants, $res[$x]);
                }
            } 
            if ($close_restaurants != 0) {
                $close_restaurants_size = count($close_restaurants);
                $rand = rand(0, $close_restaurants_size);
                $result = array(
                    "status" => "success",
                    "message" => "Data fetched successfully",
                    "name" => $row['name'],
                    "GPS" => $row['GPS'],
                    "description" => $row['description'],
                    "grade" => $row['grade'],
                    "photo" => $row['photo'],
    
                );
            } else $result = array("status" => "failed", "message" => "No close restaurants");
        } else $result = array("status" => "failed", "message" => "Database empty");
    } else $result = array("status" => "failed", "message" => "Database connection failed");
} else $result = array("status" => "failed", "message" => "GPS cannot get your location");

echo json_encode($result, JSON_PRETTY_PRINT);

            
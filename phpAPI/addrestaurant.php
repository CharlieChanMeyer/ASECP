<?php
$message = "";
$con = mysqli_connect("thesis-database.cmdyp0pvnymo.ap-northeast-3.rds.amazonaws.com:3306", "root", "J58myKHNAAihjSX", "asecp");
if(!empty($_POST['name']) && !empty($_POST['grade']) && !empty($_POST['description']) && !empty($_POST['photo']) && !empty($_POST['gps'])){

    $name=$_POST['name'];
    $grade=floatval($_POST['grade']);
    $description=$_POST['description'];
    $image=$_POST['photo'];
    $gps=$_POST['gps'];

    $picture_name='images'.date("d-m-y").'-'.time().'-'.rand(1,1000).'.png';
    $path='uploads/'.$picture_name;
        if(file_put_contents($path,
            base64_decode($image))){ $message .= 'Image : success | ';}
        else $message .= 'Image : failed to insert in server | ';
    
    if($con){
        $sql="INSERT INTO restaurants (name, grade, description, photo, gps) VALUES ('$name',$grade,'$description', '$picture_name', '$gps')";
        if (mysqli_query($con,$sql)) {
            $message .= "BDD : Success insert text";
        } else $message .= "BDD : Failed to insert in Database |  ('$name',$grade,'$description', '$picture_name','$gps')";
    }

}
else $message .= "All fields required!";
echo $message;
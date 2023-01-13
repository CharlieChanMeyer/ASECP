<?php
//Add a new restaurant to the app
$message = "";
$con = mysqli_connect("thesis-database.cmdyp0pvnymo.ap-northeast-3.rds.amazonaws.com:3306", "root", "J58myKHNAAihjSX", "asecp"); //link to the database
if(!empty($_POST['name']) && !empty($_POST['grade']) && !empty($_POST['description']) && !empty($_POST['photo']) && !empty($_POST['gps'])){

    //Variables
    $name=$_POST['name']; //restaurant's name
    $grade=floatval($_POST['grade']); //restaurant's rate
    $description=$_POST['description']; //restaurant's description or opinion
    $image=$_POST['photo']; //restaurant's photo
    $gps=$_POST['gps']; //restaurant's location

    $picture_name='images'.date("d-m-y").'-'.time().'-'.rand(1,1000).'.png'; //rename the image

    //Upload the image to the server
    $path='uploads/'.$picture_name;
        if(file_put_contents($path,
            base64_decode($image))){ $message .= 'Image : success | ';}
        else $message .= 'Image : failed to insert in server | ';
    
    //Insert data to the database
    if($con){ 
        $sql="INSERT INTO restaurants (name, grade, description, photo, gps) VALUES ('$name',$grade,'$description', '$picture_name', '$gps')";
        if (mysqli_query($con,$sql)) {
            $message .= "BDD : Success insert text";
        } else $message .= "BDD : Failed to insert in Database |  ('$name',$grade,'$description', '$picture_name','$gps')";
    }
}
else $message .= "All fields required!";
echo $message; //display message
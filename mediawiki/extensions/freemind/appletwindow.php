<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Mind Map Java Browser</title>
</head>
<body>
<?php
 if (isset($_GET['browsemode_initial_map'])) :
 require_once("appletwindowFunction.php");
?>
<style type="text/css">
body { margin-left:0px; margin-right:0px; margin-top:0px; margin-bottom:0px }
</style>
<?php
if(isset($_GET['mm_title'])){
$mm_title = $_GET['mm_title'];
unset($_GET['mm_title']);
}
else{
	$mm_title = "open";
}
$path = dirname($_SERVER['PHP_SELF']).'/';
print getMindMapAppletOutput($mm_title, $_GET, "100%", $path);
  else :
?>
Do not call this page directly !
<?php
  endif;
?>
</body>
</html>

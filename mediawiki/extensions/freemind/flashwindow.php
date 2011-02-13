<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Mind Map Flash Viewer</title>
</head>
<body>
<?php
 require_once("flashwindowFunction.php");
 if (isset($_GET[initLoadFile]) && isset($_GET[startCollapsedToLevel])) :
?>
<style type="text/css">
/* hide from ie on mac \*/
html {
height: 100%;
overflow: hidden;
}
#flashcontent {
height: 100%;
}
/* end hide */
body {
height: 100%;
margin: 0;
padding: 0;
background-color: #ffffff;
}
</style>
<?php
if(isset($_GET['mm_title'])){
$mm_title = $_GET['mm_title'];
unset($_GET['mm_title']);
}
else{
	$mm_title = "open";
}
 print getMindMapFlashOutput($mm_title, $_GET, "", "100%", "");
  else :
?>
Do not call this page directly !
<?php
  endif;
?>
</body>
</html>

<?php
function getMindMapFlashOutput($mm_title, $parameters, $flashContentCounter, $mm_height, $path) {
$output = '';
if($mm_title != ""){
	$output='
<p style="text-align:center"><a href="'.$parameters['initLoadFile'].'">'.$mm_title.'</a></p>';
}
	$output.='
<script type="text/javascript" src="'.$path.'flashobject.js"></script>'
.'<div id="flashcontent'.$flashContentCounter.'"> Flash plugin or Javascript are turned off. Activate both  and reload to view the mindmap</div>
<script type="text/javascript">
// <![CDATA[
var fo = new FlashObject("'.$path.'visorFreemind.swf", "'.$path.'visorFreeMind", "100%", "'.$mm_height.'", 6, "'.$mm_bgcolor.'");
fo.addParam("quality", "high");
fo.addParam("bgcolor", "#ffffff");
';
	foreach ($parameters as $key => $value)
		$output.="fo.addVariable(\"$key\", \"$value\");\n";
	$output.='fo.addVariable("openUrl", "_blank");
fo.write("flashcontent'.$flashContentCounter.'");
// ]]>
</script>';
	return $output;
}
?>
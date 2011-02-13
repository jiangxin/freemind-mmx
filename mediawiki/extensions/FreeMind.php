<?php
// Freemind mindmap WikiMedia extension
// (C) Dimitry Polivaev 2006
// Example:
$wgExtensionFunctions[] = "wfFreemindExtension";

function wfFreemindExtension()
{
    global $wgParser;
    // Defines the tag <mindmap> ... </mindmap>
    // The second parameter is the callback function for
    // processing the text between the tags
    $wgParser->setHook("mm", "renderMindmap");
}
// The callback function for converting the input text to HTML output
function renderMindmap($input)
{
    // Default parameter values:
    $mm_height = "450";
    $mm_type = "flash";
    $mm_target = "embedded";

    if (preg_match('/^\s*\[{2}\s*:\s*(\w.*)\]{2}\s*$/', $input, $matches)) {
        $mm_target = "link";
        $input = $matches[1];
    } else
    if (preg_match('/^\s*\[{2}\s*(\w.*)\]{2}\s*$/', $input, $matches)) {
        $mm_target = "embedded";
        $input = $matches[1];
    } else{
    	return MindmapHelp($input);
	}

    $mm_title = "";
    $mm_description = "";

    $paramVector = explode("|", $input);
    $url = $paramVector[0];
    $paramNumber = count($paramVector);
    for ($i = 1; $i < $paramNumber; $i++) {
        $param = trim($paramVector[$i]);
        if (preg_match('/^\s*[0-9]+p[xt]$/', $param)) {
        	$mm_height = $param;
        }
        else if (preg_match('/^(\w+)\s+(.*)$/', $param, $pair)) {
            if ("title" === $pair[1]) {
                $mm_title = $pair[2];
            } else if ("parameters" === $pair[1]) {
                preg_match_all('/(\\w+?)\\s*=\\s*"(.+?)"/', $pair[2], $match, PREG_SET_ORDER);
                foreach ($match as $i) $params[$i[1]] = $i[2];
                preg_match_all('/(\\w+?)\s*=\s*([^"\s]+?)/', $pair[2], $match, PREG_SET_ORDER);
                foreach ($match as $i) $params[$i[1]] = $i[2];
            } else {
                if ($mm_description != "")
                    $mm_description .= '|';
                $mm_description .= $param;
            }
        } else {
            if ("flash" === $param || "applet" === $param) {
                $mm_type = $param;
            } elseif ("notitle" === $param) {
                $mm_notitle = 1;
            } else {
                $mm_description .= $param;
            }
        }
    }

    if ($mm_description === "") {
        $mm_description = $url;
    }

	if($mm_notitle){
		$mm_title = "";
	}
    elseif ($mm_title === "") {
        $mm_title = $url;
    }
    $imageTitle = Title::makeTitleSafe("Image", $url);
    if($imageTitle == NULL){
    	return MindmapNotFoundError($url);
	}
    $img = Image::newFromTitle($imageTitle);
    if($img->exists() != true){
    	return MindmapNotFoundError($url);
	}
    $url = $img->getViewURL(false);

    global $wgServer, $wgScriptPath, $wgTitle, $wgUrlProtocols, $wgUser;
    static $flashContentCounter = 0;
    if ($mm_type === "flash") {
        $params['initLoadFile'] = $url;
        if (isset($params['openUrl'])) unset($params['openUrl']);
        if (! isset($params['startCollapsedToLevel'])) $params['startCollapsedToLevel'] = "5";
        if (strcasecmp($mm_target, "embedded") == 0) {
            $flashContentCounter++;
            require_once("freemind/flashwindowFunction.php");
            $output = getMindMapFlashOutput($mm_title, $params, $flashContentCounter, $mm_height, "$wgScriptPath/extensions/freemind/");
        } else if (strcasecmp($mm_target, "link") == 0) {
            $Formcounter++;
            $ref = "$wgScriptPath/extensions/freemind/flashwindow.php?";
        } else {
            $output = MindmapHelp($url);
        }
    } else if ($mm_type === "applet") {
        $server = $_SERVER['SERVER_NAME'];
        $params['browsemode_initial_map'] = "http://$server$url";
        if (isset($params['type'])) unset($params['type']);
        if (isset($params['scriptable'])) unset($params['scriptable']);
        if (isset($params['modes'])) unset($params['modes']);
        if (isset($params['initial_mode'])) unset($params['initial_mode']);

        if (strcasecmp($mm_target, "embedded") == 0) {
            require_once("freemind/appletwindowFunction.php");
            $output = getMindMapAppletOutput($mm_title, $params, $mm_height, "$wgScriptPath/extensions/freemind/");
        } else if (strcasecmp($mm_target, "link") == 0) {
            $ref = "$wgScriptPath/extensions/freemind/appletwindow.php?";
        } else {
            $output = MindmapHelp($url);
        }
    } else {
        $output = MindmapHelp($url);
    }
    if (! isset($output) && $mm_target === "link") {
        $params['mm_title'] = rawurlencode($mm_title);
        foreach ($params as $key => $value) {
            $ref .= "$key=$value&";
        }
        $ref = substr($ref, 0, -1);
        $output .= "<a href=$ref>$mm_description</a>";
    }
    // print($output);
    if ($mm_target == "embedded")
        $output = "$output";
    return $output;
}

function MindmapHelp($input)
{
    return '<div style=\'border: solid red 1px\'>
<p align=center><b>Ebbedded Mind Map Syntax error in </b>: <code>&lt;mm&gt;'.$input.'&lt;/mm&gt;</code></p><br>
<p><b>&nbsp;Syntax: </b>
<blockquote><b><code>&lt;mm&gt;[[{name}|{options}|parameters {parameters}]]&lt;/mm&gt;</code></b><br>
<b><code>&lt;mm&gt;[[:{name}|{options}|parameters {parameters}]]&lt;/mm&gt;</code></b></blockquote>
<b>&nbsp;Examples:</b>
<blockquote>
<ul>
<li><code>&lt;mm&gt;[[Hello.mm]]&lt;/mm&gt;</code>
<li><code>&lt;mm&gt;[[Hello.mm|flash]]&lt;/mm&gt;</code>
<li><code>&lt;mm&gt;[[Hello.mm|applet]]&lt;/mm&gt;</code>
<li><code>&lt;mm&gt;[[Hello.mm|flash|80pt]]&lt;/mm&gt;</code>
<li><code>&lt;mm&gt;[[Hello.mm|applet|150px|title example map]]&lt;/mm&gt;</code>
<li><code>&lt;mm&gt;[[:Hello.mm]]&lt;/mm&gt;</code>
<li><code>&lt;mm&gt;[[:Hello.mm|description]]&lt;/mm&gt;</code>
<li><code>&lt;mm&gt;[[:Hello.mm|flash|title the map in flash|map in flash]]&lt;/mm&gt;</code>
</ul></blockquote>
</div>';
}

function MindmapNotFoundError($input)
{
    return '<div style=\'border: solid red 1px\'>
<p align=center><b>Error: Mind Map file <code>'.$input.'</code> not found </b> </p><br>
</div>';
}
?>

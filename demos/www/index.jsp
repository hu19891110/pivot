<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

<html>
<head>
<title>Pivot Demos - Index</title>
<link rel="stylesheet" href="demo.css"/>
<link rel="icon" href="favicon.ico"/>
<link rel="shortcut icon" href="favicon.ico"/>
<style>
body {
    font-family:Verdana;
    font-size:11px;
}

p.caption {
    font-style:italic;
    padding-top:0px;
}

p.command {
    font-family:"Consolas", "Monaco", "Bitstream Vera Sans Mono", "Courier New";
    background-color:#E7E5DC;
    padding-top:12px;
    padding-left:24px;
    padding-bottom:12px;
    padding-right:24px;
}

pre.snippet {
    padding:6px;
    border:#E7E5DC solid 1px;
    font-family:"Consolas", "Monaco", "Bitstream Vera Sans Mono", "Courier New";
    font-size:1em;
}

tt {
    font-family:"Consolas", "Monaco", "Bitstream Vera Sans Mono", "Courier New";
}

table {
    font-size:11px;
}

img {
    vertical-align: middle;
}
</style>
<script src="http://java.com/js/deployJava.js"></script>
<%@ include file="jnlp_common.jsp" %>
</head>
<body>
<h2>Demos</h2>
<p>This page contains a collection of Pivot demos. All demos require Java 6 or greater.</p>
<br/>

<h3><a href="kitchen_sink.html">"Kitchen Sink"</a></h3>
<p>Demonstrates a number of commonly used Pivot components.</p>
<p><img src="kitchen_sink.png"></p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>kitchen_sink.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;,&nbsp;
Signed:&nbsp;
<script>
var url = "<%= codebase %>kitchen_sink.signed.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;,&nbsp;
Signed with Custom Colors:&nbsp;
<script>
var url = "<%= codebase %>kitchen_sink.custom_colors.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="stock_tracker.html">Stock Tracker</a></h3>
<p>An example of a simple but practical "real world" application built using
Pivot. Monitors stock quotes provided by <a href="http://finance.yahoo.com/">Yahoo!
Finance</a>.</p>
<p><img src="stock_tracker.png"></p>
<br/>
<div>
Signed:&nbsp;
<script>
var url = "<%= codebase %>stock_tracker.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;,&nbsp;
Signed with French Locale (fr):&nbsp;
<script>
var url = "<%= codebase %>stock_tracker_fr.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="itunes_search.html">iTunes Search</a></h3>
<p>Simple application that allows a user to run search queries against the
iTunes Music Store and presents the results in a table view.</p>
<p><img src="itunes_search.png"></p>
<br/>
<div>
Signed:&nbsp;
<script>
var url = "<%= codebase %>itunes_search.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="http://ixnay.biz/charts.html">Charting</a> <img src="page_go.png"></h3>
<p>Demonstrates charting in Pivot using the JFreeChart chart provider (hosted at http://ixnay.biz).</p>
<p><img src="charts.png"></p>
<hr/>

<h3><a href="http://www.satelliteconsulting.com/">News Ticker/Slide Show</a> <img src="page_go.png"></h3>
<p>Demonstrates Pivot used in a real-world context, used to show a news feed
and a slide show of client logos (hosted at http://www.satelliteconsulting.com).</p>
<p><img src="sci.png"></p>
<hr/>

<h3><a href="json_viewer.html">JSON Viewer</a></h3>
<p>Allows users to visually browse a JSON structure using a TreeView component.</p>
<p><img src="json_viewer.png"></p>
<br/>
<div>
Signed:&nbsp;
<script>
var url = "<%= codebase %>json_viewer.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="scripting.html">Scripting</a></h3>
<p>Simple example of a Pivot application written using JavaScript.</p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>scripting.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="file_drag_drop.html">File Drag &amp; Drop</a></h3>
<p>Demonstrates Pivot's support for drag and drop.</p>
<br/>
<div>
Signed:&nbsp;
<script>
var url = "<%= codebase %>file_drag_drop.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="table_row_editor.html">Table Row Editor</a></h3>
<p>Example of a table row editor that uses a "Family Feud"-like flip effect to
edit rows.</p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>table_row_editor.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="animated_clock.html">Animated Clock</a></h3>
<p>Demonstrates Pivot's MovieView component, which is used to present a clock
constructed using Pivot's drawing API.</p>
<p><img src="clock.png"></p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>animated_clock.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="large_data.html">Large Data Sets</a></h3>
<p>Demonstrates Pivot's ability to handle large data sets of up to 1,000,000
rows.</p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>large_data.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="rss_feed.html">RSS Feed</a></h3>
<p>Demonstrates how to build a simple RSS client in Pivot.</p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>rss_feed.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="dom_interaction.html">DOM Interaction</a></h3>
<p>Demonstrates Pivot's support for bi-directional communication between a Pivot
application and the browser DOM.</p>
<br/>
<hr/>

<h3><a href="decorators.html">Decorators</a></h3>
<p>Demonstrates the use of "decorators" in Pivot. Decorators allow a developer to
attach additional presentation to components, such as drop shadows, reflections,
image effects, etc. This example shows a window with a reflection decorator and
a frame with a fade decorator.</p>
<p><img src="decorators.png"></p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>decorators.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="fixed_column_table.html">Fixed Column Table</a></h3>
<p>Explains how to create a table with fixed columns in Pivot. Fixed columns are
handy when displaying tables with many columns.</p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>fixed_column_table.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<h3><a href="multiselect.html">Multiple Selection</a></h3>
<p>Demonstrates Pivot's use of ranges to maintain selection state in a ListView
component. This is more efficient than maintaining a list of individual
selected indexes.</p>
<br/>
<div>
Unsigned:&nbsp;
<script>
var url = "<%= codebase %>multiselect.jnlp";
deployJava.createWebStartLaunchButton(url, '1.6');
</script>
&nbsp;&nbsp;&nbsp;
</div>
<hr/>

<br/>

</body>
</html>
<#assign basePath=request.contextPath>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Pragma" content="no-cache">
<title>走势分析图</title>
<link href="${basePath}/css/style.css" rel="stylesheet" type="text/css">
<link href="${basePath}/css/line.css" rel="stylesheet" type="text/css">
<link href="${basePath}/js/jqueryui/jquery-ui-1.8.23.custom.css" rel="stylesheet" type="text/css">

<script language="javascript" type="text/javascript" src="${basePath}/js/jquery-1.8.0.min.js"></script>
<script language="javascript" type="text/javascript" src="${basePath}/js/line.js"></script>
<script language="javascript" type="text/javascript" src="${basePath}/js/jqueryui/jquery-ui-1.8.23.custom.min.js"></script>
	
</head>
<body style="background: none;">	
	<div id="content">
		<div class="search">
			<b class="b5"></b> 
			<b class="b6"></b> 
			<b class="b7"></b> 
			<b class="b8"></b>
			<table width="100%" id="titlemessage" border="0" cellpadding="0" cellspacing="0" style="background: #DDE0E5;">
				<tbody>
					<tr>
						<td><b><span class="redtext">高频彩基本走势</span></b></td>
						<td><b>
							<select id="typeid" name="typeid">
							    <option value="ffc">高频彩</option>
							    <option value="lfc">双分彩</option>
							    <option value="yfc">分分彩</option>
							    <option value="ssc" selected>重庆时时彩</option>
							  </select>
						</b></td>
						<td>
							<a href="#" id="display_30" class="ml10" target="_self"  num="30">最近30期</a> 
							<a href="#" class="ml10" target="_self" num="50">最近50期</a> 
							<a href="#" class="ml10" target="_self" num="80">最近80期</a>
							<a href="#" class="ml10" target="_self" num="100">最近100期</a>
							<a href="#" class="ml10" target="_self" num="120">最近120期</a>
						</td>
						<td></td>
					</tr>

				</tbody>
			</table>
			<b class="b8"></b> <b class="b7"></b> <b class="b6"></b> <b
				class="b5"></b>
		</div>
		<table height="5">
			<tbody>
				<tr>
					<td></td>
				</tr>
			</tbody>
		</table>
		<table align="center">
			<tbody>
				<tr>
					<td colspan="3" style="border: 0px;">标注形式选择&nbsp;<input
						type="checkbox" name="checkbox2" value="checkbox" id="has_line">
						<span><b><label for="has_line">显示走势折线</label></b></span>&nbsp; <span>
							<label for="no_miss"> <input type="checkbox"
								name="checkbox" value="checkbox" id="no_miss">不带遗漏
						</label>
					</span>
					</td>
				</tr>
			</tbody>
		</table>
		<table height="5">
			<tbody>
				<tr>
					<td></td>
				</tr>
			</tbody>
		</table>
		<div style="position: relative; height: 2688px;" id="container">
			''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
			<table id="chartsTable" width="100%" cellpadding="0" cellspacing="0"
				border="0" style="position: absolute; top: 0; left: 0;">
				<tbody>
					<tr id="title">
						<td rowspan="2"><strong>期号</strong></td>
						<td rowspan="2" colspan="5" class="redtext"><strong>开奖号码</strong></td>
						<td colspan="10"><strong>万位</strong></td>
						<td colspan="10"><strong>千位</strong></td>
						<td colspan="10"><strong>百位</strong></td>
						<td colspan="10"><strong>十位</strong></td>
						<td colspan="10"><strong>个位</strong></td>
					</tr>
					<tr id="head">
						<td class="wdh" align="center"><strong>0</strong></td>
						<td class="wdh" align="center"><strong>1</strong></td>
						<td class="wdh" align="center"><strong>2</strong></td>
						<td class="wdh" align="center"><strong>3</strong></td>
						<td class="wdh" align="center"><strong>4</strong></td>
						<td class="wdh" align="center"><strong>5</strong></td>
						<td class="wdh" align="center"><strong>6</strong></td>
						<td class="wdh" align="center"><strong>7</strong></td>
						<td class="wdh" align="center"><strong>8</strong></td>
						<td class="wdh" align="center"><strong>9</strong></td>
						<td class="wdh" align="center"><strong>0</strong></td>
						<td class="wdh" align="center"><strong>1</strong></td>
						<td class="wdh" align="center"><strong>2</strong></td>
						<td class="wdh" align="center"><strong>3</strong></td>
						<td class="wdh" align="center"><strong>4</strong></td>
						<td class="wdh" align="center"><strong>5</strong></td>
						<td class="wdh" align="center"><strong>6</strong></td>
						<td class="wdh" align="center"><strong>7</strong></td>
						<td class="wdh" align="center"><strong>8</strong></td>
						<td class="wdh" align="center"><strong>9</strong></td>
						<td class="wdh" align="center"><strong>0</strong></td>
						<td class="wdh" align="center"><strong>1</strong></td>
						<td class="wdh" align="center"><strong>2</strong></td>
						<td class="wdh" align="center"><strong>3</strong></td>
						<td class="wdh" align="center"><strong>4</strong></td>
						<td class="wdh" align="center"><strong>5</strong></td>
						<td class="wdh" align="center"><strong>6</strong></td>
						<td class="wdh" align="center"><strong>7</strong></td>
						<td class="wdh" align="center"><strong>8</strong></td>
						<td class="wdh" align="center"><strong>9</strong></td>
						<td class="wdh" align="center"><strong>0</strong></td>
						<td class="wdh" align="center"><strong>1</strong></td>
						<td class="wdh" align="center"><strong>2</strong></td>
						<td class="wdh" align="center"><strong>3</strong></td>
						<td class="wdh" align="center"><strong>4</strong></td>
						<td class="wdh" align="center"><strong>5</strong></td>
						<td class="wdh" align="center"><strong>6</strong></td>
						<td class="wdh" align="center"><strong>7</strong></td>
						<td class="wdh" align="center"><strong>8</strong></td>
						<td class="wdh" align="center"><strong>9</strong></td>
						<td class="wdh" align="center"><strong>0</strong></td>
						<td class="wdh" align="center"><strong>1</strong></td>
						<td class="wdh" align="center"><strong>2</strong></td>
						<td class="wdh" align="center"><strong>3</strong></td>
						<td class="wdh" align="center"><strong>4</strong></td>
						<td class="wdh" align="center"><strong>5</strong></td>
						<td class="wdh" align="center"><strong>6</strong></td>
						<td class="wdh" align="center"><strong>7</strong></td>
						<td class="wdh" align="center"><strong>8</strong></td>
						<td class="wdh" align="center"><strong>9</strong></td>
					</tr>
					
					<#list list as rec>
    					<tr>
    						<td id="title">${rec.expect}</td>
							<#list rec.codes?split(",") as num>
						        <td class="wdh" align="center"><div class="ball02">${num}</div></td>
						     </#list>
						     
						     <#list rec.codes?split(",") as num2>						     
						     	<#list  '0,1,2,3,4,5,6,7,8,9'?split(',') as num1>
						     		<#if num1 == num2>
											<td class="charball" align="center"><div class="ball01">${num2}</div></td>
										<#else> 
											<td class="wdh" align="center"><div class="ball03"></div></td>
									</#if>
						     	</#list>
						     </#list>
						</tr>
					</#list>
					
					<tr>
						<td nowrap="">出现总次数</td>
						<td align="center" colspan="5">&nbsp;</td>
						<td align="center">6</td>
						<td align="center">15</td>
						<td align="center">11</td>
						<td align="center">7</td>
						<td align="center">9</td>
						<td align="center">11</td>
						<td align="center">16</td>
						<td align="center">9</td>
						<td align="center">15</td>
						<td align="center">21</td>
						<td align="center">15</td>
						<td align="center">12</td>
						<td align="center">9</td>
						<td align="center">10</td>
						<td align="center">10</td>
						<td align="center">12</td>
						<td align="center">10</td>
						<td align="center">15</td>
						<td align="center">15</td>
						<td align="center">12</td>
						<td align="center">12</td>
						<td align="center">14</td>
						<td align="center">23</td>
						<td align="center">11</td>
						<td align="center">9</td>
						<td align="center">11</td>
						<td align="center">8</td>
						<td align="center">12</td>
						<td align="center">9</td>
						<td align="center">11</td>
						<td align="center">14</td>
						<td align="center">20</td>
						<td align="center">11</td>
						<td align="center">12</td>
						<td align="center">11</td>
						<td align="center">13</td>
						<td align="center">7</td>
						<td align="center">9</td>
						<td align="center">12</td>
						<td align="center">11</td>
						<td align="center">15</td>
						<td align="center">9</td>
						<td align="center">10</td>
						<td align="center">13</td>
						<td align="center">15</td>
						<td align="center">15</td>
						<td align="center">13</td>
						<td align="center">10</td>
						<td align="center">8</td>
						<td align="center">12</td>
					</tr>
					<tr>
						<td nowrap="">平均遗漏值</td>
						<td align="center" colspan="5">&nbsp;</td>
						<td align="center">17</td>
						<td align="center">7</td>
						<td align="center">10</td>
						<td align="center">15</td>
						<td align="center">12</td>
						<td align="center">10</td>
						<td align="center">7</td>
						<td align="center">12</td>
						<td align="center">7</td>
						<td align="center">5</td>
						<td align="center">7</td>
						<td align="center">9</td>
						<td align="center">12</td>
						<td align="center">10</td>
						<td align="center">10</td>
						<td align="center">9</td>
						<td align="center">10</td>
						<td align="center">7</td>
						<td align="center">7</td>
						<td align="center">9</td>
						<td align="center">9</td>
						<td align="center">8</td>
						<td align="center">5</td>
						<td align="center">10</td>
						<td align="center">12</td>
						<td align="center">10</td>
						<td align="center">13</td>
						<td align="center">9</td>
						<td align="center">12</td>
						<td align="center">10</td>
						<td align="center">8</td>
						<td align="center">5</td>
						<td align="center">10</td>
						<td align="center">9</td>
						<td align="center">10</td>
						<td align="center">8</td>
						<td align="center">15</td>
						<td align="center">12</td>
						<td align="center">9</td>
						<td align="center">10</td>
						<td align="center">7</td>
						<td align="center">12</td>
						<td align="center">10</td>
						<td align="center">8</td>
						<td align="center">7</td>
						<td align="center">7</td>
						<td align="center">8</td>
						<td align="center">10</td>
						<td align="center">13</td>
						<td align="center">9</td>
					</tr>
					<tr>
						<td nowrap="">最大遗漏值</td>
						<td align="center" colspan="5">&nbsp;</td>
						<td align="center">49</td>
						<td align="center">22</td>
						<td align="center">19</td>
						<td align="center">32</td>
						<td align="center">34</td>
						<td align="center">24</td>
						<td align="center">37</td>
						<td align="center">54</td>
						<td align="center">24</td>
						<td align="center">16</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">0</td>
						<td align="center">39</td>
						<td align="center">18</td>
						<td align="center">22</td>
						<td align="center">21</td>
						<td align="center">23</td>
						<td align="center">36</td>
						<td align="center">24</td>
						<td align="center">21</td>
						<td align="center">28</td>
						<td align="center">39</td>
						<td align="center">33</td>
						<td align="center">16</td>
						<td align="center">27</td>
						<td align="center">36</td>
						<td align="center">39</td>
						<td align="center">38</td>
						<td align="center">36</td>
						<td align="center">33</td>
						<td align="center">25</td>
						<td align="center">27</td>
						<td align="center">30</td>
						<td align="center">25</td>
						<td align="center">32</td>
						<td align="center">21</td>
						<td align="center">26</td>
						<td align="center">20</td>
						<td align="center">45</td>
						<td align="center">47</td>
						<td align="center">37</td>
						<td align="center">28</td>

					</tr>
					<tr>
						<td nowrap="">最大连出值</td>
						<td align="center" colspan="5">&nbsp;</td>
						<td align="center">1</td>
						<td align="center">1</td>
						<td align="center">1</td>
						<td align="center">1</td>
						<td align="center">1</td>
						<td align="center">2</td>
						<td align="center">2</td>
						<td align="center">1</td>
						<td align="center">1</td>
						<td align="center">3</td>
						<td align="center">15</td>
						<td align="center">12</td>
						<td align="center">9</td>
						<td align="center">10</td>
						<td align="center">10</td>
						<td align="center">12</td>
						<td align="center">10</td>
						<td align="center">15</td>
						<td align="center">15</td>
						<td align="center">12</td>
						<td align="center">1</td>
						<td align="center">4</td>
						<td align="center">2</td>
						<td align="center">2</td>
						<td align="center">1</td>
						<td align="center">2</td>
						<td align="center">1</td>
						<td align="center">2</td>
						<td align="center">2</td>
						<td align="center">3</td>
						<td align="center">3</td>
						<td align="center">2</td>
						<td align="center">2</td>
						<td align="center">2</td>
						<td align="center">1</td>
						<td align="center">2</td>
						<td align="center">1</td>
						<td align="center">2</td>
						<td align="center">1</td>
						<td align="center">1</td>
						<td align="center">2</td>
						<td align="center">1</td>
						<td align="center">1</td>
						<td align="center">2</td>
						<td align="center">3</td>
						<td align="center">1</td>
						<td align="center">2</td>
						<td align="center">2</td>
						<td align="center">1</td>
						<td align="center">2</td>
					</tr>
					<tr id="Tr1">
						<td rowspan="2" align="center"><strong>期号</strong></td>
						<td rowspan="2" align="center" colspan="5"><strong>开奖号码</strong></td>
						<td align="center"><strong>0</strong></td>
						<td align="center"><strong>1</strong></td>
						<td align="center"><strong>2</strong></td>
						<td align="center"><strong>3</strong></td>
						<td align="center"><strong>4</strong></td>
						<td align="center"><strong>5</strong></td>
						<td align="center"><strong>6</strong></td>
						<td align="center"><strong>7</strong></td>
						<td align="center"><strong>8</strong></td>
						<td align="center"><strong>9</strong></td>
						<td align="center"><strong>0</strong></td>
						<td align="center"><strong>1</strong></td>
						<td align="center"><strong>2</strong></td>
						<td align="center"><strong>3</strong></td>
						<td align="center"><strong>4</strong></td>
						<td align="center"><strong>5</strong></td>
						<td align="center"><strong>6</strong></td>
						<td align="center"><strong>7</strong></td>
						<td align="center"><strong>8</strong></td>
						<td align="center"><strong>9</strong></td>
						<td align="center"><strong>0</strong></td>
						<td align="center"><strong>1</strong></td>
						<td align="center"><strong>2</strong></td>
						<td align="center"><strong>3</strong></td>
						<td align="center"><strong>4</strong></td>
						<td align="center"><strong>5</strong></td>
						<td align="center"><strong>6</strong></td>
						<td align="center"><strong>7</strong></td>
						<td align="center"><strong>8</strong></td>
						<td align="center"><strong>9</strong></td>
						<td align="center"><strong>0</strong></td>
						<td align="center"><strong>1</strong></td>
						<td align="center"><strong>2</strong></td>
						<td align="center"><strong>3</strong></td>
						<td align="center"><strong>4</strong></td>
						<td align="center"><strong>5</strong></td>
						<td align="center"><strong>6</strong></td>
						<td align="center"><strong>7</strong></td>
						<td align="center"><strong>8</strong></td>
						<td align="center"><strong>9</strong></td>
						<td align="center"><strong>0</strong></td>
						<td align="center"><strong>1</strong></td>
						<td align="center"><strong>2</strong></td>
						<td align="center"><strong>3</strong></td>
						<td align="center"><strong>4</strong></td>
						<td align="center"><strong>5</strong></td>
						<td align="center"><strong>6</strong></td>
						<td align="center"><strong>7</strong></td>
						<td align="center"><strong>8</strong></td>
						<td align="center"><strong>9</strong></td>
					</tr>
					<tr id="Tr2">
						<td colspan="10"><strong>万位</strong></td>
						<td colspan="10"><strong>千位</strong></td>
						<td colspan="10"><strong>百位</strong></td>
						<td colspan="10"><strong>十位</strong></td>
						<td colspan="10"><strong>个位</strong></td>
					</tr>

				</tbody>
			</table>
		</div>

		<dl class="tips">
			<dt>图表参数说明</dt>
			<dd>出现总次数：统计期数内实际出现的次数。</dd>
			<dd>平均遗漏值：统计期数内遗漏的平均值。（计算公式：平均遗漏＝统计期数/(出现次数+1)。）</dd>
			<dd>最大遗漏值：统计期数内遗漏的最大值。</dd>
			<dd>最大连出值：统计期数内连续开出的最大值。</dd>
		</dl>
	</div>
	<!--<div id="footer">Copyright © 娱乐</div>-->
	
	<script language="javascript">
		fw.onReady(function() {
			Chart.init();
			DrawLine.bind("chartsTable", "has_line");
			DrawLine.color('#499495');
			DrawLine.add((parseInt(0) * 10 + 5 + 1), 2, 10, 0);
			DrawLine.color('#E4A8A8');
			DrawLine.add((parseInt(1) * 10 + 5 + 1), 2, 10, 0);
			DrawLine.color('#499495');
			DrawLine.add((parseInt(2) * 10 + 5 + 1), 2, 10, 0);
			DrawLine.color('#E4A8A8');
			DrawLine.add((parseInt(3) * 10 + 5 + 1), 2, 10, 0);
			DrawLine.color('#499495');
			DrawLine.add((parseInt(4) * 10 + 5 + 1), 2, 10, 0);
			DrawLine.draw(Chart.ini.default_has_line);
			if ($("#chartsTable").width() > $('body').width()) {
				$('body').width($("#chartsTable").width() + "px");
			}
			$("#container").height($("#chartsTable").height() + "px");
			resize();

			var nols = $(".ball04,.ball03");
			$("#no_miss").click(function() {

				var checked = $(this).attr("checked");
				$.each(nols, function(i, n) {
					if (checked == true || checked == 'checked') {
						n.style.display = 'none';
					} else {
						n.style.display = 'block';
					}
				});
			});
		});
		function resize() {
			window.onresize = func;
			function func() {
				window.location.href = window.location.href;
			}
		}
		$(function() {
			$(".datetxt").datepicker({
				onSelect : function(dateText, inst) {
					$(this).val(dateText);
				}
			});
		})
	</script>

	<script type="text/javascript">
		$(function(){
		    var typeid=location.href;
			var typeidCurr=$("#typeid").val();
			var startInd = typeid.indexOf("typeid=");
			var endInd = typeid.indexOf("&num");
			if(endInd < 0){
	        	typeid = typeid.substring(startInd+7);
	        }else{
	        	typeid = typeid.substring(startInd+7, endInd);
	        }
			if(typeid!="undefined" && typeid != typeidCurr){
				$("#typeid").val(typeid);
			}
			
			$("a").click(function(){
				var typeid=$("#typeid").val();
				var num = $(this).attr("num");
				var url ="${basePath}/app/analysis/trendsAnalysis?typeid="+typeid+"&num="+num;
				$(this).attr("href",url);
				
				changeCss($(this));
			});
		});
		
		function changeCss(selEle){
			$("a").each(function(){
					$(this).removeClass("on");
				});
				
			selEle.addClass("on");
		}
		/*function queryTrendAnalysis(limitAmount){
			var typeid=$("#typeid").val();
			var num = $("#display_30").attr("num");
			var url ="${basePath}/app/analysis/trendsAnalysis?typeid="+typeid+"&num="+num;
			$("#display_30").attr("href",url);
			$("#display_30").click();
		}*/
		
		
	</script>

	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 137px; left: 403px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 158px; left: 403px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 177px; left: 404px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 200px; left: 445px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 221px; left: 445px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 238px; left: 342px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 260px; left: 342px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 286px; left: 397px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 305px; left: 382px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 322px; left: 384px;"></canvas>
	<canvas width="173" height="19"
		style="position: absolute; visibility: visible; top: 342px; left: 279px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 363px; left: 279px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 386px; left: 405px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 412px; left: 460px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 433px; left: 460px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 447px; left: 321px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 468px; left: 321px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 494px; left: 445px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 513px; left: 404px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 536px; left: 382px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 554px; left: 321px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 578px; left: 298px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 594px; left: 300px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 616px; left: 363px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 637px; left: 279px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 659px; left: 279px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 681px; left: 299px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 700px; left: 300px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 722px; left: 405px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 743px; left: 405px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 764px; left: 342px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 786px; left: 299px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 809px; left: 298px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 827px; left: 321px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 848px; left: 321px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 867px; left: 321px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 888px; left: 300px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 914px; left: 277px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 933px; left: 278px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 952px; left: 321px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 972px; left: 279px;"></canvas>
	<canvas width="173" height="19"
		style="position: absolute; visibility: visible; top: 993px; left: 279px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1016px; left: 405px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1038px; left: 362px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1058px; left: 300px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 1077px; left: 300px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 1098px; left: 321px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 1119px; left: 321px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 1140px; left: 321px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1166px; left: 319px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 1183px; left: 342px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1205px; left: 384px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1231px; left: 376px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1246px; left: 384px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 1266px; left: 300px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 1287px; left: 300px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1311px; left: 383px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1331px; left: 384px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1352px; left: 384px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1372px; left: 300px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1393px; left: 300px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1414px; left: 384px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1436px; left: 405px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1460px; left: 403px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1477px; left: 342px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1498px; left: 342px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1523px; left: 403px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1542px; left: 404px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 1560px; left: 321px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1582px; left: 321px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1609px; left: 397px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1625px; left: 405px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1651px; left: 460px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 1665px; left: 321px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 1686px; left: 321px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1710px; left: 425px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1733px; left: 403px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 1750px; left: 300px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1772px; left: 300px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1794px; left: 320px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 1812px; left: 321px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1835px; left: 405px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 1855px; left: 300px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1877px; left: 300px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 1897px; left: 363px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1918px; left: 384px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1943px; left: 361px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1961px; left: 300px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 1980px; left: 300px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 2001px; left: 300px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2027px; left: 277px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2048px; left: 277px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 2064px; left: 300px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2087px; left: 384px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 2107px; left: 300px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2129px; left: 300px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2150px; left: 363px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 2170px; left: 342px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 2191px; left: 342px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 2211px; left: 321px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2237px; left: 298px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 2253px; left: 300px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 2277px; left: 425px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2300px; left: 424px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2318px; left: 384px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 2338px; left: 279px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 2359px; left: 279px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 2380px; left: 363px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2405px; left: 424px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2423px; left: 363px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 2443px; left: 363px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 2464px; left: 363px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2486px; left: 300px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 2505px; left: 300px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2531px; left: 445px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2552px; left: 445px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2573px; left: 445px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 2589px; left: 342px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 2611px; left: 342px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 137px; left: 487px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 153px; left: 510px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 179px; left: 634px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 196px; left: 531px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 216px; left: 531px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 242px; left: 655px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 259px; left: 552px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 281px; left: 489px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 301px; left: 489px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 324px; left: 530px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 345px; left: 488px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 368px; left: 487px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 386px; left: 510px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 408px; left: 572px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 426px; left: 489px;"></canvas>
	<canvas width="173" height="19"
		style="position: absolute; visibility: visible; top: 447px; left: 489px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 473px; left: 655px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 489px; left: 510px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 517px; left: 502px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 531px; left: 510px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 557px; left: 634px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 576px; left: 593px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 595px; left: 510px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 616px; left: 510px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 639px; left: 551px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 659px; left: 489px;"></canvas>
	<canvas width="173" height="19"
		style="position: absolute; visibility: visible; top: 678px; left: 489px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 704px; left: 655px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 721px; left: 552px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 744px; left: 551px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 763px; left: 594px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 786px; left: 635px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 805px; left: 531px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 827px; left: 531px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 851px; left: 571px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 868px; left: 573px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 893px; left: 634px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 909px; left: 489px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 930px; left: 489px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 956px; left: 613px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 973px; left: 510px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 996px; left: 509px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1015px; left: 552px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 1036px; left: 531px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1061px; left: 508px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1080px; left: 509px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1099px; left: 552px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1124px; left: 613px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 1140px; left: 489px;"></canvas>
	<canvas width="173" height="19"
		style="position: absolute; visibility: visible; top: 1161px; left: 489px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 1182px; left: 510px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1208px; left: 487px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 1224px; left: 489px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1250px; left: 655px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 1266px; left: 531px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1290px; left: 488px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1310px; left: 489px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1331px; left: 552px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1355px; left: 592px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1372px; left: 594px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1395px; left: 635px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1418px; left: 613px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1439px; left: 592px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 1456px; left: 489px;"></canvas>
	<canvas width="173" height="19"
		style="position: absolute; visibility: visible; top: 1476px; left: 489px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 1497px; left: 531px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1521px; left: 488px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 1539px; left: 489px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1565px; left: 634px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1588px; left: 628px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1604px; left: 573px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1625px; left: 573px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 1645px; left: 531px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1667px; left: 531px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1689px; left: 551px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 1707px; left: 552px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 1731px; left: 635px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 1749px; left: 510px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1772px; left: 510px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1793px; left: 573px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1817px; left: 613px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 1833px; left: 489px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 1855px; left: 489px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1877px; left: 531px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1898px; left: 531px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1919px; left: 594px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 1939px; left: 573px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1964px; left: 550px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1982px; left: 552px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2006px; left: 592px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 2023px; left: 594px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 2043px; left: 510px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 2064px; left: 510px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2090px; left: 655px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 2109px; left: 614px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2134px; left: 607px;"></canvas>
	<canvas width="110" height="19"
		style="position: absolute; visibility: visible; top: 2148px; left: 489px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 2170px; left: 489px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 2193px; left: 572px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2216px; left: 592px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 2233px; left: 489px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 2253px; left: 489px;"></canvas>
	<canvas width="152" height="19"
		style="position: absolute; visibility: visible; top: 2274px; left: 489px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2300px; left: 487px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 2319px; left: 509px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 2338px; left: 552px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 2358px; left: 510px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 2379px; left: 510px;"></canvas>
	<canvas width="68" height="17"
		style="position: absolute; visibility: visible; top: 2401px; left: 573px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2426px; left: 550px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 2445px; left: 551px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2468px; left: 571px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2486px; left: 573px;"></canvas>
	<canvas width="89" height="17"
		style="position: absolute; visibility: visible; top: 2506px; left: 531px;"></canvas>
	<canvas width="131" height="19"
		style="position: absolute; visibility: visible; top: 2526px; left: 531px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2552px; left: 655px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2575px; left: 649px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2594px; left: 634px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2612px; left: 573px;"></canvas>
	<canvas width="135" height="19"
		style="position: absolute; visibility: visible; top: 132px; left: 699px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 156px; left: 698px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 181px; left: 733px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 198px; left: 698px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 219px; left: 698px;"></canvas>
	<canvas width="49" height="17"
		style="position: absolute; visibility: visible; top: 238px; left: 741px;"></canvas>
	<canvas width="70" height="17"
		style="position: absolute; visibility: visible; top: 259px; left: 720px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 284px; left: 718px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 307px; left: 733px;"></canvas>
	<canvas width="115" height="19"
		style="position: absolute; visibility: visible; top: 321px; left: 741px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 349px; left: 864px;"></canvas>
	<canvas width="115" height="19"
		style="position: absolute; visibility: visible; top: 363px; left: 741px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 391px; left: 733px;"></canvas>
	<canvas width="49" height="17"
		style="position: absolute; visibility: visible; top: 406px; left: 741px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 431px; left: 804px;"></canvas>
	<canvas width="71" height="17"
		style="position: absolute; visibility: visible; top: 448px; left: 741px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 471px; left: 698px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 492px; left: 698px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 517px; left: 733px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 536px; left: 739px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 557px; left: 739px;"></canvas>
	<canvas width="93" height="17"
		style="position: absolute; visibility: visible; top: 574px; left: 741px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 595px; left: 784px;"></canvas>
	<canvas width="48" height="15"
		style="position: absolute; visibility: visible; top: 617px; left: 720px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 641px; left: 718px;"></canvas>
	<canvas width="71" height="17"
		style="position: absolute; visibility: visible; top: 658px; left: 741px;"></canvas>
	<canvas width="113" height="19"
		style="position: absolute; visibility: visible; top: 678px; left: 699px;"></canvas>
	<canvas width="69" height="17"
		style="position: absolute; visibility: visible; top: 700px; left: 699px;"></canvas>
	<canvas width="69" height="17"
		style="position: absolute; visibility: visible; top: 721px; left: 699px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 743px; left: 699px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 767px; left: 760px;"></canvas>
	<canvas width="48" height="15"
		style="position: absolute; visibility: visible; top: 785px; left: 720px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 811px; left: 712px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 832px; left: 712px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 853px; left: 712px;"></canvas>
	<canvas width="48" height="15"
		style="position: absolute; visibility: visible; top: 869px; left: 720px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 893px; left: 760px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 911px; left: 761px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 931px; left: 806px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 956px; left: 870px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 977px; left: 870px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 995px; left: 849px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1016px; left: 849px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1035px; left: 762px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1063px; left: 754px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1078px; left: 762px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1100px; left: 783px;"></canvas>
	<canvas width="29" height="15"
		style="position: absolute; visibility: visible; top: 1121px; left: 740px;"></canvas>
	<canvas width="137" height="19"
		style="position: absolute; visibility: visible; top: 1140px; left: 741px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1163px; left: 849px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1189px; left: 842px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1208px; left: 826px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1229px; left: 804px;"></canvas>
	<canvas width="70" height="17"
		style="position: absolute; visibility: visible; top: 1246px; left: 720px;"></canvas>
	<canvas width="114" height="19"
		style="position: absolute; visibility: visible; top: 1266px; left: 720px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1294px; left: 842px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1310px; left: 849px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1336px; left: 886px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1357px; left: 886px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1372px; left: 806px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1393px; left: 806px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1414px; left: 806px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1439px; left: 782px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1460px; left: 760px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1481px; left: 739px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1502px; left: 718px;"></canvas>
	<canvas width="158" height="19"
		style="position: absolute; visibility: visible; top: 1518px; left: 720px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1539px; left: 762px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 1562px; left: 699px;"></canvas>
	<canvas width="113" height="19"
		style="position: absolute; visibility: visible; top: 1581px; left: 699px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1607px; left: 826px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1630px; left: 842px;"></canvas>
	<canvas width="135" height="19"
		style="position: absolute; visibility: visible; top: 1644px; left: 699px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 1670px; left: 697px;"></canvas>
	<canvas width="158" height="19"
		style="position: absolute; visibility: visible; top: 1686px; left: 720px;"></canvas>
	<canvas width="137" height="19"
		style="position: absolute; visibility: visible; top: 1707px; left: 741px;"></canvas>
	<canvas width="71" height="17"
		style="position: absolute; visibility: visible; top: 1729px; left: 741px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1751px; left: 827px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1777px; left: 864px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1793px; left: 827px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1814px; left: 783px;"></canvas>
	<canvas width="48" height="15"
		style="position: absolute; visibility: visible; top: 1835px; left: 720px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1861px; left: 712px;"></canvas>
	<canvas width="136" height="19"
		style="position: absolute; visibility: visible; top: 1875px; left: 720px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1896px; left: 762px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1918px; left: 762px;"></canvas>
	<canvas width="93" height="17"
		style="position: absolute; visibility: visible; top: 1939px; left: 741px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1966px; left: 733px;"></canvas>
	<canvas width="29" height="15"
		style="position: absolute; visibility: visible; top: 1982px; left: 740px;"></canvas>
	<canvas width="48" height="15"
		style="position: absolute; visibility: visible; top: 2003px; left: 720px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2027px; left: 697px;"></canvas>
	<canvas width="157" height="19"
		style="position: absolute; visibility: visible; top: 2043px; left: 699px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 2064px; left: 762px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2090px; left: 739px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2111px; left: 739px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2132px; left: 739px;"></canvas>
	<canvas width="29" height="15"
		style="position: absolute; visibility: visible; top: 2150px; left: 740px;"></canvas>
	<canvas width="48" height="15"
		style="position: absolute; visibility: visible; top: 2171px; left: 720px;"></canvas>
	<canvas width="70" height="17"
		style="position: absolute; visibility: visible; top: 2191px; left: 720px;"></canvas>
	<canvas width="49" height="17"
		style="position: absolute; visibility: visible; top: 2212px; left: 741px;"></canvas>
	<canvas width="49" height="17"
		style="position: absolute; visibility: visible; top: 2233px; left: 741px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 2254px; left: 806px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2279px; left: 870px;"></canvas>
	<canvas width="137" height="19"
		style="position: absolute; visibility: visible; top: 2295px; left: 741px;"></canvas>
	<canvas width="71" height="17"
		style="position: absolute; visibility: visible; top: 2317px; left: 741px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2342px; left: 804px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2365px; left: 798px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2381px; left: 805px;"></canvas>
	<canvas width="114" height="19"
		style="position: absolute; visibility: visible; top: 2400px; left: 720px;"></canvas>
	<canvas width="9" height="9"
		style="position: absolute; visibility: visible; top: 2426px; left: 697px;"></canvas>
	<canvas width="91" height="17"
		style="position: absolute; visibility: visible; top: 2443px; left: 699px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2464px; left: 806px;"></canvas>
	<canvas width="179" height="19"
		style="position: absolute; visibility: visible; top: 2484px; left: 699px;"></canvas>
	<canvas width="135" height="19"
		style="position: absolute; visibility: visible; top: 2505px; left: 699px;"></canvas>
	<canvas width="93" height="17"
		style="position: absolute; visibility: visible; top: 2527px; left: 741px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 2550px; left: 698px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2570px; left: 699px;"></canvas>
	<canvas width="47" height="15"
		style="position: absolute; visibility: visible; top: 2591px; left: 699px;"></canvas>
	<canvas width="28" height="13"
		style="position: absolute; visibility: visible; top: 2613px; left: 698px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 133px; left: 1026px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 154px; left: 1004px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 175px; left: 938px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 196px; left: 938px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 217px; left: 916px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 237px; left: 916px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 263px; left: 1046px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 281px; left: 1047px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 300px; left: 938px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 326px; left: 914px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 343px; left: 916px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 365px; left: 1003px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 385px; left: 982px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 406px; left: 982px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 426px; left: 938px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 452px; left: 914px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 473px; left: 914px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 489px; left: 938px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 512px; left: 1003px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 532px; left: 1004px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 557px; left: 1090px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 573px; left: 960px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 599px; left: 936px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 620px; left: 936px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 641px; left: 958px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 662px; left: 958px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 680px; left: 959px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 701px; left: 959px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 725px; left: 936px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 748px; left: 930px;"></canvas>
	<canvas width="160" height="19"
		style="position: absolute; visibility: visible; top: 762px; left: 938px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 783px; left: 982px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 804px; left: 982px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 827px; left: 1069px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 853px; left: 1062px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 869px; left: 1069px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 888px; left: 982px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 909px; left: 982px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 931px; left: 1026px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 952px; left: 960px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 973px; left: 960px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 998px; left: 1024px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1016px; left: 981px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1037px; left: 981px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1063px; left: 1018px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1078px; left: 938px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1098px; left: 938px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1121px; left: 1069px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1141px; left: 1048px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1161px; left: 916px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1189px; left: 908px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1208px; left: 914px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1225px; left: 938px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1246px; left: 938px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1266px; left: 938px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1289px; left: 1047px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1308px; left: 982px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1331px; left: 937px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1357px; left: 930px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1373px; left: 937px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1393px; left: 916px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 1413px; left: 916px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1441px; left: 1062px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1455px; left: 938px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1481px; left: 914px;"></canvas>
	<canvas width="182" height="19"
		style="position: absolute; visibility: visible; top: 1497px; left: 916px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1518px; left: 1004px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1541px; left: 959px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1565px; left: 936px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1582px; left: 938px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1604px; left: 981px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1624px; left: 916px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1646px; left: 915px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1667px; left: 915px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1691px; left: 914px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1714px; left: 930px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1730px; left: 937px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1756px; left: 974px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1775px; left: 958px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1796px; left: 958px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1814px; left: 981px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1834px; left: 1026px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1856px; left: 1047px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1876px; left: 960px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1903px; left: 952px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1922px; left: 936px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1939px; left: 938px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1964px; left: 1002px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1985px; left: 1002px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2006px; left: 1002px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2023px; left: 1004px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2044px; left: 1004px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2065px; left: 1004px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2090px; left: 1090px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2107px; left: 1026px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2128px; left: 938px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 2148px; left: 938px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2174px; left: 1068px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2195px; left: 1090px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2216px; left: 1090px;"></canvas>
	<canvas width="160" height="19"
		style="position: absolute; visibility: visible; top: 2232px; left: 916px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2255px; left: 915px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 2275px; left: 960px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2296px; left: 1026px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2317px; left: 1026px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 2338px; left: 1026px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2363px; left: 1068px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2381px; left: 1069px;"></canvas>
	<canvas width="182" height="19"
		style="position: absolute; visibility: visible; top: 2400px; left: 916px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2428px; left: 908px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2449px; left: 908px;"></canvas>
	<canvas width="160" height="19"
		style="position: absolute; visibility: visible; top: 2463px; left: 916px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 2484px; left: 938px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2510px; left: 914px;"></canvas>
	<canvas width="182" height="19"
		style="position: absolute; visibility: visible; top: 2526px; left: 916px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 2547px; left: 1004px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2573px; left: 980px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2591px; left: 937px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 2611px; left: 938px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 134px; left: 1179px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 158px; left: 1200px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 174px; left: 1202px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 195px; left: 1202px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 217px; left: 1136px;"></canvas>
	<canvas width="160" height="19"
		style="position: absolute; visibility: visible; top: 237px; left: 1136px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 258px; left: 1202px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 281px; left: 1157px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 305px; left: 1156px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 322px; left: 1180px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 344px; left: 1267px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 363px; left: 1158px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 384px; left: 1158px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 407px; left: 1289px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 433px; left: 1326px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 448px; left: 1246px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 469px; left: 1246px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 491px; left: 1289px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 510px; left: 1158px;"></canvas>
	<canvas width="160" height="19"
		style="position: absolute; visibility: visible; top: 531px; left: 1158px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 552px; left: 1224px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 575px; left: 1223px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 595px; left: 1202px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 616px; left: 1202px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 637px; left: 1268px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 657px; left: 1224px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 679px; left: 1224px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 700px; left: 1246px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 720px; left: 1136px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 741px; left: 1136px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 767px; left: 1266px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 790px; left: 1282px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 805px; left: 1224px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 825px; left: 1224px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 847px; left: 1268px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 872px; left: 1266px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 888px; left: 1158px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 914px; left: 1134px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 930px; left: 1136px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 958px; left: 1260px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 972px; left: 1158px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 998px; left: 1156px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1019px; left: 1178px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1036px; left: 1202px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1057px; left: 1180px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1078px; left: 1180px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1103px; left: 1266px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 1119px; left: 1136px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1140px; left: 1136px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1161px; left: 1136px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1182px; left: 1136px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1203px; left: 1136px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1229px; left: 1134px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1247px; left: 1157px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1271px; left: 1178px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1292px; left: 1178px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1310px; left: 1157px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1329px; left: 1158px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1351px; left: 1202px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1376px; left: 1200px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1399px; left: 1216px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1420px; left: 1216px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1434px; left: 1224px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 1455px; left: 1180px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1476px; left: 1180px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1498px; left: 1224px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1518px; left: 1224px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1541px; left: 1289px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1561px; left: 1224px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 1586px; left: 1222px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1603px; left: 1158px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 1623px; left: 1158px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1645px; left: 1246px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1666px; left: 1180px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1687px; left: 1180px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1708px; left: 1180px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1729px; left: 1180px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1750px; left: 1246px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 1771px; left: 1224px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1791px; left: 1224px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1812px; left: 1202px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1840px; left: 1194px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1855px; left: 1136px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1877px; left: 1135px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 1897px; left: 1180px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 1919px; left: 1201px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 1945px; left: 1194px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 1959px; left: 1202px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 1980px; left: 1224px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2002px; left: 1136px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2029px; left: 1128px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 2043px; left: 1136px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2069px; left: 1244px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2086px; left: 1158px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2107px; left: 1158px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2132px; left: 1222px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2155px; left: 1216px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2171px; left: 1223px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 2191px; left: 1268px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2212px; left: 1246px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 2232px; left: 1136px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2255px; left: 1135px;"></canvas>
	<canvas width="10" height="9"
		style="position: absolute; visibility: visible; top: 2279px; left: 1178px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2297px; left: 1201px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 2316px; left: 1136px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2344px; left: 1128px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2359px; left: 1136px;"></canvas>
	<canvas width="2" height="5"
		style="position: absolute; visibility: visible; top: 2386px; left: 1216px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2401px; left: 1136px;"></canvas>
	<canvas width="116" height="19"
		style="position: absolute; visibility: visible; top: 2421px; left: 1136px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2444px; left: 1267px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2465px; left: 1267px;"></canvas>
	<canvas width="50" height="17"
		style="position: absolute; visibility: visible; top: 2485px; left: 1268px;"></canvas>
	<canvas width="72" height="17"
		style="position: absolute; visibility: visible; top: 2506px; left: 1246px;"></canvas>
	<canvas width="94" height="19"
		style="position: absolute; visibility: visible; top: 2526px; left: 1136px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 2547px; left: 1136px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 2568px; left: 1136px;"></canvas>
	<canvas width="138" height="19"
		style="position: absolute; visibility: visible; top: 2589px; left: 1136px;"></canvas>
	<canvas width="30" height="15"
		style="position: absolute; visibility: visible; top: 2612px; left: 1245px;"></canvas>
</body>
</html>
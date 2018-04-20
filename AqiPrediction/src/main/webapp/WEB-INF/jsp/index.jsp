<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/4/17
  Time: 22:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>Particle Web</title>

    <!-- Load c3.css -->
    <link href="${pageContext.request.contextPath}/css/c3.css" rel="stylesheet">


    <link href="${pageContext.request.contextPath}/css/loaders.min.css" rel="stylesheet">

    <!-- Load d3.js and c3.js -->
    <script src="https://d3js.org/d3.v5.js" charset="UTF-8"></script>
    <script src="${pageContext.request.contextPath}/js/c3.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>

    <script src="${pageContext.request.contextPath}/js/loaders.css.js"></script>

    <style>
        * {
            margin: 0;
            padding: 0;
        }

        .container {
            background: transparent;
        }

        .title {
            z-index: 3;

            text-align: center;
            font: bold 80px/1 "Helvetica Neue", Helvetica, Arial, sans-serif;
            color: #fff;
            text-shadow: 0 1px 0 #cccccc, 0 2px 0 #c9c9c9, 0 3px 0 #bbbbbb, 0 4px 0 #b9b9b9, 0 5px 0 #aaaaaa, 0 6px 1px rgba(0, 0, 0, 0.1), 0 0 5px rgba(0, 0, 0, 0.1), 0 1px 3px rgba(0, 0, 0, 0.3), 0 3px 5px rgba(0, 0, 0, 0.2), 0 5px 10px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.2), 0 20px 20px rgba(0, 0, 0, 0.15);
            -webkit-transition: .2s all linear;

        }

        .title_div {
            z-index: 3;
            position: relative;
            top: 20px;
        }

        canvas {
            z-index: -1;
        }

        select{
            width: 100px;
            height: 30px;
        }

        #btn{
            width: 100px;
            height: 30px;
        }

        .chart {
            width: 800px;
            height: 300px;
            background: transparent;
            margin: 20px auto 0;
            z-index: 3;

        }

        .prediction_box,.real_box{
            margin-top: 15px;
            background: whitesmoke;
            text-align: center;
        }

        .prediction_box h2{
            color: springgreen;
            font-size: 50px;
            font-family: "Microsoft Himalaya";
        }

        .real_box h2{
            font-size: 50px;
            color: maroon;
            font-family: "Microsoft Himalaya";
        }

        #chart_predict {
            margin-top: 20px;
            background: whitesmoke;
        }

        #chart_real {
            margin-top: 20px;
            background: whitesmoke;
        }

        #loading{
            display: none;
            margin-left: 30px;
        }
    </style>
</head>
<body>

<div class="container">

    <canvas height="620" width="1360" id="canvas" style="position: absolute; height: 100%;"></canvas>

    <div class="title_div">
        <h1 class="title">Beijing AQI Prediction</h1>

        <div>
            <div class="chart">

                <form id="form" method="post">
                    <select id="location">
                        <option value="万寿西宫">万寿西宫</option>
                        <option value="东四">东四</option>
                        <option value="农展馆">农展馆</option>
                        <option value="古城">古城</option>
                        <option value="天坛">天坛</option>
                        <option value="奥体中心">奥体中心</option>
                        <option value="官园">官园</option>
                        <option value="定陵">定陵</option>
                        <option value="怀柔镇">怀柔镇</option>
                        <option value="昌平镇">昌平镇</option>
                        <option value="海淀区万柳">海淀区万柳</option>
                        <option value="顺义新城">顺义新城</option>
                    </select>

                    <select id="year">
                        <option value="2018">2018</option>
                        <option value="2017">2017</option>
                        <option value="2016">2016</option>
                        <option value="2015">2015</option>
                    </select>

                    <select id="month">
                        <option value="01">01</option>
                        <option value="02">02</option>
                        <option value="03">03</option>
                        <option value="04">04</option>
                        <option value="05">05</option>
                        <option value="06">06</option>
                        <option value="07">07</option>
                        <option value="08">08</option>
                        <option value="09">09</option>
                        <option value="10">10</option>
                        <option value="11">11</option>
                        <option value="12">12</option>
                    </select>


                    <select id="day">
                        <option value="01">01</option>
                        <option value="02">02</option>
                        <option value="03">03</option>
                        <option value="04">04</option>
                        <option value="05">05</option>
                        <option value="06">06</option>
                        <option value="07">07</option>
                        <option value="08">08</option>
                        <option value="09">09</option>
                        <option value="10">10</option>
                        <option value="11">11</option>
                        <option value="12">12</option>
                        <option value="13">13</option>
                        <option value="14">14</option>
                        <option value="15">15</option>
                        <option value="16">16</option>
                        <option value="17">17</option>
                        <option value="18">18</option>
                        <option value="19">19</option>
                        <option value="20">20</option>
                        <option value="21">21</option>
                        <option value="22">22</option>
                        <option value="23">23</option>
                        <option value="24">24</option>
                        <option value="25">25</option>
                        <option value="26">26</option>
                        <option value="27">27</option>
                        <option value="28">28</option>
                        <option value="29">29</option>
                        <option value="30">30</option>
                        <option value="31">31</option>
                    </select>

                    <button type="button" id="btn">Predict</button>
                    <div id="loading" class="loader-inner ball-spin-fade-loader"></div>
                </form>

                <div class="prediction_box">
                    <h2>Prediction</h2>
                    <div id="chart_predict"></div>
                </div>

                <div class="real_box">
                    <h2>Real</h2>
                    <div id="chart_real"></div>
                </div>
            </div>
        </div>

    </div>

</div>


<script src="${pageContext.request.contextPath}/js/index.js" charset="utf-8"></script>

</body>
</html>
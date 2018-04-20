(function () {
    var canvasEl = document.getElementById('canvas');
    var ctx = canvasEl.getContext('2d');
    var mousePos = [0, 0];

    var easingFactor = 5.0;
    var backgroundColor = '#000';
    var nodeColor = '#fff';
    var edgeColor = '#fff';

    var nodes = [];
    var edges = [];

    function constructNodes() {
        for (var i = 0; i < 100; i++) {
            var node = {
                drivenByMouse: i == 0,
                x: Math.random() * canvasEl.width,
                y: Math.random() * canvasEl.height,
                vx: Math.random() * 1 - 0.5,
                vy: Math.random() * 1 - 0.5,
                radius: Math.random() > 0.9 ? 3 + Math.random() * 3 : 1 + Math.random() * 3
            };

            nodes.push(node);
        }

        nodes.forEach(function (e) {
            nodes.forEach(function (e2) {
                if (e == e2) {
                    return;
                }

                var edge = {
                    from: e,
                    to: e2
                }

                addEdge(edge);
            });
        });
    }

    function addEdge(edge) {
        var ignore = false;

        edges.forEach(function (e) {
            if (e.from == edge.from && e.to == edge.to) {
                ignore = true;
            }

            if (e.to == edge.from && e.from == edge.to) {
                ignore = true;
            }
        });

        if (!ignore) {
            edges.push(edge);
        }
    }

    function step() {
        nodes.forEach(function (e) {
            if (e.drivenByMouse) {
                return;
            }

            e.x += e.vx;
            e.y += e.vy;

            function clamp(min, max, value) {
                if (value > max) {
                    return max;
                } else if (value < min) {
                    return min;
                } else {
                    return value;
                }
            }

            if (e.x <= 0 || e.x >= canvasEl.width) {
                e.vx *= -1;
                e.x = clamp(0, canvasEl.width, e.x)
            }

            if (e.y <= 0 || e.y >= canvasEl.height) {
                e.vy *= -1;
                e.y = clamp(0, canvasEl.height, e.y)
            }
        });

        adjustNodeDrivenByMouse();
        render();
        window.requestAnimationFrame(step);
    }

    function adjustNodeDrivenByMouse() {
        nodes[0].x += (mousePos[0] - nodes[0].x) / easingFactor;
        nodes[0].y += (mousePos[1] - nodes[0].y) / easingFactor;
    }

    function lengthOfEdge(edge) {
        return Math.sqrt(Math.pow((edge.from.x - edge.to.x), 2) + Math.pow((edge.from.y - edge.to.y), 2));
    }

    function render() {
        ctx.fillStyle = backgroundColor;
        ctx.fillRect(0, 0, canvasEl.width, canvasEl.height);

        edges.forEach(function (e) {
            var l = lengthOfEdge(e);
            var threshold = canvasEl.width / 8;

            if (l > threshold) {
                return;
            }

            ctx.strokeStyle = edgeColor;
            ctx.lineWidth = (1.0 - l / threshold) * 2.5;
            ctx.globalAlpha = 1.0 - l / threshold;
            ctx.beginPath();
            ctx.moveTo(e.from.x, e.from.y);
            ctx.lineTo(e.to.x, e.to.y);
            ctx.stroke();
        });
        ctx.globalAlpha = 1.0;

        nodes.forEach(function (e) {
            if (e.drivenByMouse) {
                return;
            }

            ctx.fillStyle = nodeColor;
            ctx.beginPath();
            ctx.arc(e.x, e.y, e.radius, 0, 2 * Math.PI);
            ctx.fill();
        });
    }

    window.onresize = function () {
        canvasEl.width = document.body.clientWidth;
        canvasEl.height = canvasEl.clientHeight;

        if (nodes.length == 0) {
            constructNodes();
        }

        render();
    };

    window.onmousemove = function (e) {
        mousePos[0] = e.clientX;
        mousePos[1] = e.clientY;
    }

    window.onresize(); // trigger the event manually.
    window.requestAnimationFrame(step);


    function validateDateTime(testdate) {
        var date_regex = /((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])/;
        var res = date_regex.test(testdate);
        var d = new Date();
        var timenow = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
        console.log(timenow + " and " + testdate);
        if (res) {
            var ymd = testdate.match(/(\d{4})-(\d+)-(\d+)/);
            var year = parseInt(ymd[1]);
            var month = parseInt(ymd[2]);
            var day = parseInt(ymd[3]);
            if (Date.parse(timenow) === Date.parse(testdate)) {
                return "same";
            }
            if (day > 28) {
                //获取当月的最后一天
                var lastDay = new Date(year, month, 0).getDate();
                if (lastDay >= day) {
                    return (Date.parse(timenow) > Date.parse(testdate));
                }else{
                    return false;
                }
            }
            return (Date.parse(timenow) > Date.parse(testdate));
        }
        else
            return false

    }


    $("#btn").click(function () {
        var year = $("#year").val();
        var month = $("#month").val();
        var day = $("#day").val();
        var date = parseInt(year) + "-" + parseInt(month) + "-" + parseInt(day);
        console.log(date);
        var v = validateDateTime(date);
        console.log(v);
        if (v === false) {
            alert("Invalid time");
            return;
        }

        $("#loading").css("display", "inline");

        $.ajax({
            type: "POST",
            url: "/aqiPrediction/index",
            data: {
                year: $("#year").val(),
                month: $("#month").val(),
                day: $("#day").val(),
                location: $("#location").val(),
                valid: v
            },
            success: function (data) {
                //alert(data);
                var resp = JSON.parse(data);
                var predictData = ['AQI_Level'].concat(resp["predict"]);
                var realData = ['AQI_Level'].concat(resp["real"]);
                console.log(predictData);
                console.log(realData);
                chart.load({
                    columns: [predictData],
                });
                chart2.load({
                    columns: [realData]
                });

                $("#loading").css("display", "none");
            }
        })
    });


    //d3
    var predict_data = ['AQI_Level', 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6];

    var chart = c3.generate({
        size: {
            height: 200,
            width: 800
        },
        bindto: '#chart_predict',
        data: {
            columns: [
                //predict_data
            ],
            type: 'bar',
            colors: {
                AQI_Level: function (d) {
                    if (d.value === 6) return '#009966';
                    if (d.value === 5) return '#FFDE33';
                    if (d.value === 4) return '#FE9833';
                    if (d.value === 3) return '#CC0033';
                    if (d.value === 2) return '#660099';
                    if (d.value === 1) return '#7E0023';
                    return '#ff5123';
                }
            }
        },
        axis: {
            y: {
                tick: {
                    format: function (d) {
                        if (d === 1) return "hazardous";
                        if (d === 2) return "Very Unhealthy";
                        if (d === 3) return "Unhealthy";
                        if (d === 4) return "Unhealthy for Sensitive";
                        if (d === 5) return "moderate";
                        if (d === 6) return "good";
                    }
                },
                label: 'AQI_Level',
                show: true,
                max: 6,
                min: 1
            },
            x: {
                label: "Time(hour)"
            }
        },
        legend: {
            show: false
        }
    });
    var chart2 = c3.generate({
        size: {
            height: 200,
            width: 800
        },
        bindto: '#chart_real',
        data: {
            size: {
                height: 500,
                width: 800
            },
            columns: [
                //real_data
            ],
            type: 'bar',
            colors: {
                /**
                 * @return {string}
                 */
                AQI_Level: function (d) {
                    if (d.value === 6) return '#009966';
                    if (d.value === 5) return '#FFDE33';
                    if (d.value === 4) return '#FE9833';
                    if (d.value === 3) return '#CC0033';
                    if (d.value === 2) return '#660099';
                    if (d.value === 1) return '#7E0023';
                    return '#ff5123';
                }
            }
        },
        axis: {
            y: {
                tick: {
                    format: function (d) {
                        if (d === 1) return "hazardous";
                        if (d === 2) return "Very Unhealthy";
                        if (d === 3) return "Unhealthy";
                        if (d === 4) return "Unhealthy for Sensitive";
                        if (d === 5) return "moderate";
                        if (d === 6) return "good";
                    }
                },
                label: 'AQI_Level',
                show: true,
                max: 6,
                min: 1
            },
            x: {
                label: "Time(hour)"
            }
        },
        legend: {
            show: false
        }
    });


}).call(this);

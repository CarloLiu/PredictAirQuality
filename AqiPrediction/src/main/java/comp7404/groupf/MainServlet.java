package comp7404.groupf;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rocklct on 2018/4/17.
 */
@WebServlet("/index")
public class MainServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        String location = request.getParameter("location");
        String isSame = request.getParameter("valid");


        String date = year+month+day;
        ArrayList<Double> predicRs = HttpUtils.getClssifyResult(date,location);

        List<InstanceObject> todayList = new ArrayList<>();

        if(!isSame.equals("same")) {
            todayList = HttpUtils.getInstanceFromUrl(date, location);
        }
        List<Double> realList = new ArrayList<>();
        for (InstanceObject o : todayList) {
            double aqi = o.getAqi();
            double level = 0;
            if (aqi <= 50) level = 6;
            if (aqi > 50 && aqi <= 100) level = 5;
            if (aqi > 100 && aqi <= 150) level = 4;
            if (aqi > 150 && aqi <= 200) level = 3;
            if (aqi > 200 && aqi <= 300) level = 2;
            if (aqi > 300) level = 1;
            realList.add(level);
        }

        Map<String,List<Double>> map = new HashMap<>();
        map.put("predict",predicRs);
        map.put("real",realList);
        String testJson = JSON.toJSONString(map);
        System.out.println(testJson);
        response.getWriter().write(testJson);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //System.out.println("test");
        request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        HttpUtils.initCls(this.getServletContext().getRealPath("")+"/final.model");

    }


}

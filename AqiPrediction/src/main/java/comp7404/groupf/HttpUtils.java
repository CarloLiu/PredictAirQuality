package comp7404.groupf;

import com.alibaba.fastjson.JSON;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import javax.servlet.Servlet;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Rocklct on 2018/4/18.
 */
public class HttpUtils {
    public static Classifier cls;
    public static Instances instances;

    static {

        List<String> values = new ArrayList<>();
        values.add("北京");
        List<String> values2 = new ArrayList<>();
        List<String> values3 = new ArrayList<>();
        values2.add("万寿西宫");
        values2.add("东四");
        values2.add("农展馆");
        values2.add("古城");
        values2.add("天坛");
        values2.add("奥体中心");
        values2.add("官园");
        values2.add("定陵");
        values2.add("怀柔镇");
        values2.add("昌平镇");
        values2.add("海淀区万柳");
        values2.add("顺义新城");

        values3.add("优");
        values3.add("良");
        values3.add("轻度污染");
        values3.add("中度污染");
        values3.add("重度污染");
        values3.add("严重污染");

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Year"));
        attributes.add(new Attribute("Month"));
        attributes.add(new Attribute("Day"));
        attributes.add(new Attribute("Hour"));
        attributes.add(new Attribute("City", values));
        attributes.add(new Attribute("Site", values2));
        attributes.add(new Attribute("preMeanAqi"));
        attributes.add(new Attribute("preMeanPm25"));
        attributes.add(new Attribute("preMeanPm10"));
        attributes.add(new Attribute("preMeanCo"));
        attributes.add(new Attribute("preMeanNo2"));
        attributes.add(new Attribute("preMeanOzone1hour"));
        attributes.add(new Attribute("preMeanSo2"));
        attributes.add(new Attribute("MeanAqi"));
        attributes.add(new Attribute("MeanPm25"));
        attributes.add(new Attribute("MeanPm10"));
        attributes.add(new Attribute("MeanCo"));
        attributes.add(new Attribute("MeanNo2"));
        attributes.add(new Attribute("MeanOzone1hour"));
        attributes.add(new Attribute("MeanSo2"));
        attributes.add(new Attribute("class", values3));

        instances = new Instances("data", attributes, 0);
        instances.setClassIndex(instances.numAttributes() - 1);


    }

    public static void initCls(String path) {
        ObjectInputStream ob = null;
        try {
            ob = new ObjectInputStream(new FileInputStream(path));
            cls = (Classifier) ob.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        //System.out.println("test");
        boolean tests;

        String today = "20180301";
        initCls("./j48.model");
        ArrayList<Double> rs = getClssifyResult(today, "古城");
        for (double d : rs) {
            System.out.print(d + " ");
        }
        System.out.print("\n");

        List<InstanceObject> todayList = getInstanceFromUrl(today, "古城");
        List<Double> realList = new ArrayList<>();
        for (InstanceObject o : todayList) {
            double aqi = o.getAqi();
            double level = 0;
            if (aqi <= 50) level = 0;
            if (aqi > 50 && aqi <= 100) level = 1;
            if (aqi > 100 && aqi <= 150) level = 2;
            if (aqi > 150 && aqi <= 200) level = 3;
            if (aqi > 200 && aqi <= 300) level = 4;
            if (aqi > 300) level = 5;
            realList.add(level);
            System.out.print(level + " ");
        }

        System.out.println();

        Map<String, List<Double>> map = new HashMap<>();
        map.put("predict", rs);
        map.put("real", realList);
        String testJson = JSON.toJSONString(map);
        System.out.println(testJson);


    }

    public static ArrayList<Double> getClssifyResult(String date, String locations) {

        String today = date;
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);

        ArrayList<Double> classifyResult = new ArrayList<>();

        String day1Before = null, day2Before = null, day3Before = null,
                day4Before = null, day5Before = null;
        try {
            day1Before = getDaybefore(today);
            day2Before = getDaybefore(day1Before);
            day3Before = getDaybefore(day2Before);
            day4Before = getDaybefore(day3Before);
            day5Before = getDaybefore(day4Before);


            System.out.println(day1Before + " " + day2Before + " " + day3Before);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<InstanceObject> day1BeforeList = getInstanceFromUrl(day1Before, locations);
        List<InstanceObject> day2BeforeList = getInstanceFromUrl(day2Before, locations);
        List<InstanceObject> day3BeforeList = getInstanceFromUrl(day3Before, locations);
        List<InstanceObject> day4BeforeList = getInstanceFromUrl(day4Before, locations);
        List<InstanceObject> day5BeforeList = getInstanceFromUrl(day5Before, locations);

        // new model parameter
        List<List<InstanceObject>> lists = new ArrayList<>();
        lists.add(day1BeforeList);
        lists.add(day2BeforeList);
        lists.add(day3BeforeList);
        lists.add(day4BeforeList);
        lists.add(day5BeforeList);

        boolean isfind = false;
        double preMeanAqi = -1;
        double preMeanPm25 = -1;
        double preMeanPm10 = -1;
        double preMeanCo = -1;
        double preMeanNo2 = -1;
        double preMeanOzone = -1;
        double preMeanSo2 = -1;

        //preAqi
        for (List<InstanceObject> iobList : lists) {
            if (isfind) break;
            preMeanAqi = 0;
            int count = 0;
            for (int i = 0; i < 24; i++) {
                InstanceObject iob = iobList.get(i);
                if (!iob.isTag()) continue;
                if (iob.getAqi() != -1) {
                    isfind = true;
                    preMeanAqi += iob.getAqi();
                    count++;
                }
            }
            if (count != 0) {
                preMeanAqi = preMeanAqi / count;
            }

        }

        //prePm25
        for (List<InstanceObject> iobList : lists) {
            if (isfind) break;
            preMeanPm25 = 0;
            int count = 0;
            for (int i = 0; i < 24; i++) {
                InstanceObject iob = iobList.get(i);
                if (!iob.isTag()) continue;
                if (iob.getPm25() != -1) {
                    isfind = true;
                    preMeanPm25 += iob.getPm25();
                    count++;
                }
            }
            if (count != 0) {
                preMeanPm25 = preMeanPm25 / count;
            }

        }

        //Pm10
        for (List<InstanceObject> iobList:lists){
            if(isfind) break;
            preMeanPm10 = 0;
            int count = 0;
            for(int i=0;i<24;i++){
                InstanceObject iob = iobList.get(i);
                if(!iob.isTag()) continue;
                if(iob.getPm10() != -1){
                    isfind = true;
                    preMeanPm10 += iob.getPm10();
                    count++;
                }
            }
            if(count!=0){
                preMeanPm10 = preMeanPm10 / count;
            }

        }

        // preCo
        for (List<InstanceObject> iobList:lists){
            if(isfind) break;
            preMeanCo = 0;
            int count = 0;
            for(int i=0;i<24;i++){
                InstanceObject iob = iobList.get(i);
                if(!iob.isTag()) continue;
                if(iob.getCo() != -1){
                    isfind = true;
                    preMeanCo += iob.getCo();
                    count++;
                }
            }
            if(count!=0){
                preMeanCo = preMeanCo / count;
            }

        }

        //So2
        for (List<InstanceObject> iobList:lists){
            if(isfind) break;
            preMeanSo2 = 0;
            int count = 0;
            for(int i=0;i<24;i++){
                InstanceObject iob = iobList.get(i);
                if(!iob.isTag()) continue;
                if(iob.getSo2() != -1){
                    isfind = true;
                    preMeanSo2 += iob.getSo2();
                    count++;
                }
            }
            if(count!=0){
                preMeanSo2 = preMeanSo2 / count;
            }

        }

        for (List<InstanceObject> iobList:lists){
            if(isfind) break;
            preMeanNo2 = 0;
            int count = 0;
            for(int i=0;i<24;i++){
                InstanceObject iob = iobList.get(i);
                if(!iob.isTag()) continue;
                if(iob.getNo2() != -1){
                    isfind = true;
                    preMeanNo2 += iob.getNo2();
                    count++;
                }
            }
            if(count!=0){
                preMeanNo2 = preMeanNo2 / count;
            }

        }

        for (List<InstanceObject> iobList:lists){
            if(isfind) break;
            preMeanOzone = 0;
            int count = 0;
            for(int i=0;i<24;i++){
                InstanceObject iob = iobList.get(i);
                if(!iob.isTag()) continue;
                if(iob.getOzone() != -1){
                    isfind = true;
                    preMeanOzone += iob.getOzone();
                    count++;
                }
            }
            if(count!=0){
                preMeanOzone = preMeanOzone / count;
            }

        }





        for (int i = 0; i < 24; i++) {
            DenseInstance instance = new DenseInstance(28);
            ArrayList<InstanceObject> iobList = new ArrayList<>();
            iobList.add(day1BeforeList.get(i));
            iobList.add(day2BeforeList.get(i));
            iobList.add(day3BeforeList.get(i));
            iobList.add(day4BeforeList.get(i));
            iobList.add(day5BeforeList.get(i));

            double maxaqi = -1;
            double minaqi = -1;
            double meanaqi;
            double sumaqi = 0;
            double countaqi = 0;


            double maxPm25 = -1;
            double minPm25 = -1;
            double meanPm25;
            double sumPm25 = 0;
            double countPm25 = 0;

            double maxPm10 = -1;
            double minPm10 = -1;
            double meanPm10;
            double sumPm10 = 0;
            double countPm10 = 0;

            double maxCo = -1;
            double minCo = -1;
            double meanCo;
            double sumCo = 0;
            double countCo = 0;

            double maxNo2 = -1;
            double minNo2 = -1;
            double meanNo2;
            double sumNo2 = 0;
            double countNo2 = 0;

            double maxSo2 = -1;
            double minSo2 = -1;
            double meanSo2;
            double sumSo2 = 0;
            double countSo2 = 0;

            double maxOzone = -1;
            double minOzone = -1;
            double meanOzone;
            double sumOzone = 0;
            double countOzone = 0;


            for (InstanceObject o : iobList) {
                if (!o.isTag()) continue;
                //printInstance(o);
                double aqi = o.getAqi();
                if (aqi != -1) {
                    if (aqi > maxaqi || maxaqi == -1) maxaqi = aqi;
                    if (aqi < minaqi || minaqi == -1) minaqi = aqi;
                    sumaqi += aqi;
                    countaqi++;
                }

                double pm25 = o.getPm25();
                if (pm25 != -1) {
                    if (pm25 > maxPm25 || maxPm25 == -1) maxPm25 = pm25;
                    if (pm25 < minPm25 || minPm25 == -1) minPm25 = pm25;
                    sumPm25 += pm25;
                    countPm25++;
                }

                double pm10 = o.getPm10();
                if (pm10 != -1) {
                    if (pm10 > maxPm10 || maxPm10 == -1) maxPm10 = pm10;
                    if (pm10 < minPm10 || minPm10 == -1) minPm10 = pm10;
                    sumPm10 += pm10;
                    countPm10++;
                }

                double co = o.getCo();
                if (co != -1) {
                    if (co > maxCo || maxCo == -1) maxCo = co;
                    if (co < minCo || minCo == -1) minCo = co;
                    sumCo += co;
                    countCo++;
                }

                double No2 = o.getNo2();
                if (No2 != -1) {
                    if (No2 > maxNo2 || maxNo2 == -1) maxNo2 = No2;
                    if (No2 < minNo2 || minNo2 == -1) minNo2 = No2;
                    sumNo2 += No2;
                    countNo2++;
                }

                double So2 = o.getSo2();
                if (So2 != -1) {
                    if (So2 > maxSo2 || maxSo2 == -1) maxSo2 = So2;
                    if (So2 < minSo2 || minSo2 == -1) minSo2 = So2;
                    sumSo2 += So2;
                    countSo2++;
                }

                double Ozone = o.getOzone();
                if (Ozone != -1) {
                    if (Ozone > maxOzone || maxOzone == -1) maxOzone = Ozone;
                    if (Ozone < minOzone || minOzone == -1) minOzone = Ozone;
                    sumOzone += Ozone;
                    countOzone++;
                }

            }

            meanaqi = sumaqi / countaqi;
            meanPm25 = sumPm25 / countPm25;
            meanPm10 = sumPm10 / countPm10;
            meanCo = sumCo / countCo;
            meanNo2 = sumNo2 / countNo2;
            meanSo2 = sumSo2 / countSo2;
            meanOzone = sumOzone / countOzone;

//            System.out.println("predict: "+year + " "+ month+" " + day+" "+i);
//            System.out.println("predict: "+maxaqi + " "+ meanaqi+" " + minaqi);
//            System.out.println("predict: "+maxPm10 + " "+ meanPm10+" " + minPm10);
//            System.out.println("predict: "+maxPm25 + " "+ meanPm25+" " + minPm25);
//            System.out.println("predict: "+maxSo2 + " "+ meanSo2+" " + minSo2);
//            System.out.println("predict: "+maxCo + " "+ meanCo+" " + minCo);
//            System.out.println("predict: "+maxNo2 + " "+ meanNo2+" " + minNo2);
//            System.out.println("predict: "+maxOzone + " "+ meanOzone+" " + minOzone);


            instance.setDataset(instances);
            instance.setValue(0, Double.parseDouble(year));
            instance.setValue(1, Double.parseDouble(month));
            instance.setValue(2, Double.parseDouble(day));
            instance.setValue(3, i);
            instance.setValue(4, "北京");
            instance.setValue(5, locations);
            instance.setValue(6,preMeanAqi);
            instance.setValue(7,preMeanPm25);
            instance.setValue(8,preMeanPm10);
            instance.setValue(9,preMeanCo);
            instance.setValue(10,preMeanNo2);
            instance.setValue(11,preMeanOzone);
            instance.setValue(12,preMeanSo2);
            instance.setValue(13, meanaqi);
            instance.setValue(14, meanPm25);
            instance.setValue(15, meanPm10);
            instance.setValue(16, meanCo);
            instance.setValue(17, meanNo2);
            instance.setValue(18, meanOzone);
            instance.setValue(19, meanSo2);
            instance.setValue(20, "良");

            classifyResult.add(classifyData(instance));
        }

        return classifyResult;
    }


    public static double classifyData(DenseInstance instance) {

        try {


            return 7 - (cls.classifyInstance(instance) + 1);
            //System.out.println(cls.classifyInstance(instance));
            //System.out.println(instance.classValue());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getDaybefore(String dateInput) throws ParseException {
        String dateBefore = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(dateInput));
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        dateBefore = sdf.format(calendar.getTime());//获得前一天
        return dateBefore;

    }


    public static List<InstanceObject> getInstanceFromUrl(String date, String location) {
        URL url1 = null;
        URL url2 = null;
        int type_code = 0;

        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);

        //String date = year + month + day;

        String httpUrl1 = "http://beijingair.sinaapp.com/data/beijing/all/" + date + "/csv";
        String httpUrl2 = "http://beijingair.sinaapp.com/data/beijing/extra/" + date + "/csv";

        if (location.equals("万寿西宫")) type_code = 6;
        if (location.equals("东四")) type_code = 3;
        if (location.equals("农展馆")) type_code = 8;
        if (location.equals("古城")) type_code = 14;
        if (location.equals("天坛")) type_code = 4;
        if (location.equals("奥体中心")) type_code = 7;
        if (location.equals("官园")) type_code = 5;
        if (location.equals("定陵")) type_code = 26;
        if (location.equals("怀柔镇")) type_code = 23;
        if (location.equals("昌平镇")) type_code = 20;
        if (location.equals("海淀区万柳")) type_code = 9;
        if (location.equals("顺义新城")) type_code = 19;

        try {
            url1 = new URL(httpUrl1);
            url2 = new URL(httpUrl2);
            // extract all.csv
            URLConnection conn = url1.openConnection();
            URLConnection conn2 = url2.openConnection();
            InputStream inStream = conn.getInputStream();
            ArrayList<InstanceObject> instanceList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
            reader.readLine(); // first line is attribute,ignore
            reader2.readLine();
            String line = null;

            for (int i = 0; i < 24; i++) {
                InstanceObject instanceObject = new InstanceObject();
                instanceObject.setYear(Double.parseDouble(year));
                instanceObject.setMonth(Double.parseDouble(month));
                instanceObject.setDay(Double.parseDouble(day));
                instanceObject.setHour(i);
                instanceObject.setSite(location);
                instanceObject.setCity("北京");
                instanceObject.setTag(false);
                instanceList.add(instanceObject);

            }

            //System.out.println(httpUrl1);


            for (int i = 0; i < 24; i++) {
                line = reader.readLine();
                //System.out.println(line);
                if (line == null) break;
                String items1[] = line.split(",", -1);
                String items2[] = reader.readLine().split(",", -1);
                String items3[] = reader.readLine().split(",", -1);
                String items4[] = reader.readLine().split(",", -1);
                String items5[] = reader.readLine().split(",", -1);

                //printString(items5);
                int hour = Integer.parseInt(items1[1]);
                //System.out.println(hour);


                String itemsSo2[] = reader2.readLine().split(",", -1);
                reader2.readLine();
                String itemsNo2[] = reader2.readLine().split(",", -1);
                reader2.readLine();
                String itemsO3[] = reader2.readLine().split(",", -1);
                reader2.readLine();
                String itemsCO[] = reader2.readLine().split(",", -1);
                reader2.readLine();

                InstanceObject instanceObject = instanceList.get(hour);

                instanceObject.setAqi((!(items5[type_code].equals(""))) ? Double.parseDouble(items5[type_code]) : -1);
                instanceObject.setPm25((!(items1[type_code].equals(""))) ? Double.parseDouble(items1[type_code]) : -1);
                instanceObject.setPm10((!(items3[type_code].equals(""))) ? Double.parseDouble(items3[type_code]) : -1);


                instanceObject.setCo((!(itemsCO[type_code].equals(""))) ? Double.parseDouble(itemsCO[type_code]) : -1);
                instanceObject.setSo2((!(itemsSo2[type_code].equals(""))) ? Double.parseDouble(itemsSo2[type_code]) : -1);
                instanceObject.setNo2((!(itemsNo2[type_code].equals(""))) ? Double.parseDouble(itemsNo2[type_code]) : -1);
                instanceObject.setOzone((!(itemsO3[type_code].equals(""))) ? Double.parseDouble(itemsO3[type_code]) : -1);


                instanceObject.setTag(true);

                //instancList.add(instanceObject);
                //instancList.set(hour,instanceObject);


            }

            return instanceList;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void printString(String[] s) {
        StringBuilder ss = new StringBuilder();
        for (String st : s) {
            ss.append(" ").append(st);
        }
        System.out.println(ss.toString());
    }

    public static void printInstance(InstanceObject iob) {
        System.out.println(iob.getYear() + " " + iob.getMonth() + " " + iob.getDay() + " " + iob.getHour());
        System.out.println(iob.getCity() + " " + iob.getSite());
        System.out.println(iob.getAqi() + " " + iob.getPm10() + " " + iob.getPm25());
        System.out.println(iob.getSo2() + " " + iob.getNo2() + " " + iob.getOzone() + " " + iob.getCo());
        System.out.println();
    }


    public static double getAverage(String[] args) {
        int l = args.length;
        int count = 0;
        double sums = 0;
        for (int i = 3; i < l; i++) {
            if (!args[i].equals("")) {
                sums += Double.parseDouble(args[i]);
                count++;
            }
        }
        if (count == 0) return 0;
        else return sums / count;
    }


}

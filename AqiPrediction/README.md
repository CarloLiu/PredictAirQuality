## Beijing AQI Prediction website

The demo website is written by Java.You can click [this](http://23.83.233.218:7404/aqiPrediction/index) to visit the website.

Please make sure that you visit the website using the **Google kernel browser**.The javascript file is not compatible with IE explorer. 


### how to deploy it locally?
The project is based on Java Web, you can deploy the war file to the server that you want, such as Tomcat or GlassFish.

However, the project should be deployed to the context "/aqiPrediction", or it will have some trouble.

### Data Source
The historical data is all crawled from the website [http://beijingair.sinaapp.com/](http://beijingair.sinaapp.com/)


### Dependencies

 - C3.js
 - fastjson
 - weka
 - CyandevToys
 - jQuery

### License
GPL 3.0

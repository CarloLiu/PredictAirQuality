# PredictAirQuality
The project is the the prediction of Air Qulity in Beijing.

## Proposal ##

Detailed proposal.(ML Proposal of Group Q.md)

This project is to make air qulity predictions of 24 hours in the next day, given past 5 days air indexs of a appointed site.


We will test different classification methods and select the best method to generate the prediction model.

## Report ##

Detailed report(to be finished)

The process of the project covers four sections:gathering data,data clean,characteristic variable generation,model selection,demo applicetion,selected model explain(video).

Gathering data: Search the suitable data source and use crawel/api gathering the data.

Data Clean:The raw data may have some missing value or format error. For the format error,we use python to transfer them and set each attribute suitable data type.For the missing value,we set them with mean value interpolation of other 4 days.

Characteristic variable generation:For each air index (pm2.5,p10,so2,co,o3.etc)，we use max, min ,mean to sunmurize past 5 days data.And the final data set have 28 attributes.The generation of characteristic variable we use C++ to process the raw data(for the data scale is large,we select a relatively fast language.)

Model selcetion:We use Weka to test different typical classification model and select the best model(judged by Correctly Classified rate,Kappa statistic,Relative absolute error,Root mean squared error) After this step,we try different parameters and selected the best confidence index(J48).

Selected model explain(video):We use a easy understanding way to introduce the selected model(Decision tree/C4.5).No complicated explanation,we use video and funny example making it clear to audience.Here is the link of the video: **https://youtu.be/ac1xdH72CKA**

Demo applicetion:We develop a demo to applicate the model.The demo is based on Java,and you can visit **the website using the link:http://sakura.p2p.15cm.net:7404/aqiPrediction/index**.
You can select different site and date to show the prediction and real result.But for ‘today’,only the prediction result will be displayed. The main process of the demo code is:get the site and date,download the peivious five days and get selected site data,data clean and generate characteristic variable, use the model to predict,display the result on the Webpage.(**For details of the demo please check the file AqiPrediction**)

## Libraries Used ##
For data clean:

- python 2.7
- pandas

For data transformation:

- C++

For model build:

- Weka

For demo webpage：

- Javaweb
- C3.js
- jQuery
- fastjson

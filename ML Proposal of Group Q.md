# COMP 7404 Group Project Proposal

## (1) Group Q Member :

#### Liu Zhongyuan 3035455022 

#### Huang Jianian 3035455137

#### Li Chengtao   3035418634

#### Wu Qiwen      3035455072	

## (2) Topic :

####Nowdays, air pollution has become one of the most severe problems in the world. It is harmful to human health and ecosystems. But the evaluation of air quality is complex because it is related to many factors. In this project, our group wants to make air quality predictions by building machine learning model. We want to predict the next day's air quality index on the basis of the historical meteorological and air pollutant data. We plan to retrive historical and live data from AQI monitor sites and then extract those which are useful and meaningful to the prediction. These factors' data of the past several days from the target date are classified and used as input and the air quality of this target date is used as the output to train the model.####

## (3) Methodology:

#### At present, we have identified the problems that need to be solved and found relatively abundant data. The next important task is data exploration. As an integral part of building a model, we need to use different variables and influences to select different forecasting models in order to cater to the advantages of different model predictions. As a classification problem (predict the air quality to ‘excellent’, ‘good’, ‘bad’, etc), there are lots of models can be used, like decision tree (ID3, C4.5, CART, random forest), kNN and Naïve Bayesian Classifier. For comparing the advantages of different models, we can analyze different index parameters, such as extending F-measure and G-mean metrics to multi-class problems. This is mainly to check for possible deviations in the model by comparing the model with all possible sub-models. Cross-validation is a good way to evaluate predictive models. Divide the data set into two parts, one for training and one for verification. Measure the prediction accuracy using a simple mean squared error between the observed and predicted values. And then use Tensor Estimator API to build a DNN model.Then we can train the model and use the model to predict####

## (4) Demo to be implemented
#### After gathering and cleaning data,we will show the seleted Characteristic variable with Graph(using Python or other Visualisation method).And We will show the model code and training record of model(trend of training loss).At the end,we will use the model to show the prediction funtion. ####

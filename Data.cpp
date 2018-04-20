#include <iostream>  
#include <cstdlib>
#include <cstdio>
#include <string>  
#include <vector>  
#include <fstream>  
#include <sstream>  
  
using namespace std;  

/*
id
time
city
site
aqi
level
primary pollutant
pm2.5
pm10
co
no2
ozone1hour
ozone8hour
so2

*/

bool isLeapYear(int year){
    return year%4 == 0 && year%100 != 0 || year%400 == 0;
}

int dayInYear(int year, int month, int day){
    int num[12]= {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int result = day;
    for(int i = 0; i < month - 1; i++){
        result += num[i];
    }
    if(isLeapYear(year)){
        result++;
    }

    return result;
}

double stringConverToDouble(string s){
    return atof(s.c_str());
}

int stringConverToInt(string s){
    return atoi(s.c_str());
}

string doubleConverToString(double d){
    ostringstream os;
    if(os << d) return os.str();
    return "invalid conversion";
}

string intConverToString(int i){
    ostringstream os;
    if(os << i) return os.str();
    return "invalid conversion";
}

bool isNum(string str)  
{  
    for (int i = 0; i < str.size(); i++)
    {
        int tmp = (int)str[i];
        if ((tmp >= 48 && tmp <= 57) || tmp == 46)
        {
            continue;
        }
        else
        {
            return false;
        }
    } 
    return true;
}  
  
int main()  
{ 
    int i, j; //temp number

    ifstream inFile("Beijing.csv", ios::in);  
    string lineStr;  
    vector< vector<string> > strArray;  
    vector< vector<string> > data; 
    vector< vector<string> > result; 
    while (getline(inFile, lineStr))  
    {   
        //cout << lineStr << endl;  
        stringstream ss(lineStr);  
        string str;  
        vector<string> lineArray;  
        while (getline(ss, str, ',')){
            lineArray.push_back(str);  
            //cout<<str<<endl;
        }  
        strArray.push_back(lineArray);  
    } 
    cout<<strArray[0][0]<<endl;
    cout<<strArray[0].size()<<endl;
    cout<<strArray.size()<<endl;
    //clean all
    string temp;
    for(i = 0; i < strArray.size(); i++){
        vector<string> lineData;
        temp = strArray[i][1];
        if(i == 0)
            cout<<temp.substr(0, 4)<<endl;
        //year
        lineData.push_back(temp.substr(0, 4));
        //month
        lineData.push_back(intConverToString(stringConverToInt(temp.substr(5, 2))));
        //day
        lineData.push_back(intConverToString(stringConverToInt(temp.substr(8, 2))));
        //num of day in the year
        lineData.push_back(intConverToString(dayInYear(stringConverToInt(temp.substr(0, 4)), stringConverToInt(temp.substr(5, 2)), stringConverToInt(temp.substr(8, 2)))));
        //hour
        lineData.push_back(intConverToString(stringConverToInt(temp.substr(11, 2))));

        for(j = 2; j < strArray[i].size(); j++){
            //if(j!= 11 && j!=12){
                temp = strArray[i][j];
                lineData.push_back(temp.substr(3, temp.size() - 6));
            //} 
        }
        data.push_back(lineData);
    }
    cout<<"!"<<endl;
    //release
    for(i = 0; i < strArray.size(); i++){
        vector<string>().swap(strArray[i]);
    }
    vector< vector<string> >().swap(strArray);
    
    double sumTmp, meanTmp, tmp;
    int numTarget[5], numInYearTmp, yearTmp, yearTarget[5], id[5], count, count25, count10, counto,countPreDay;

    cout<<"!2"<<endl;
    for(i = 0; i < data.size(); i++){

        vector<string> lineResult;
        

        for(j = 0; j < 7; j++){
            lineResult.push_back(data[i][j]);
        }

        //find pre day id and year
        yearTmp = stringConverToInt(data[i][0]);

        numInYearTmp = stringConverToInt(data[i][3]);

        if(numInYearTmp < 6){
            for(j = 0; j < numInYearTmp - 1; j++){
                yearTarget[j] = yearTmp;
                numTarget[j] = numInYearTmp - j - 1;
            }
            for(j = numInYearTmp - 1; j < 5; j++){
                yearTarget[j] = yearTmp - 1;
                if(isLeapYear(yearTarget[j])){
                    numTarget[j] = 366 + numInYearTmp - j - 1;
                }
                else{
                    numTarget[j] = 365 + numInYearTmp - j - 1;
                }
            }
        }
        else{
            for(j = 0; j < 5; j++){
                yearTarget[j] = yearTmp;
                numTarget[j] = numInYearTmp - j - 1;
            }
        }
        //cout<<i<<endl;
        //find pre day data
        double sumPreDay[7];
        for(j = 0; j < 7; j++){
            sumPreDay[j] = 0;
        }

        count = 0;
        countPreDay = 0;
        if(i > 0){
            for(j = i - 1; (j >= 0) && (j > (i - 1500)); j--){
                for(int p = 0; p < 5; p++){
                    //match
                    if(stringConverToInt(data[j][0]) == yearTarget[p] && stringConverToInt(data[j][3]) == numTarget[p] && data[j][4] == data[i][4] && data[j][5] == data[i][5] && data[j][6] == data[i][6]){
                        id[count] = j;
                        count++;
                    }

                }
                
                if(j > (i - 576)){
                    if(stringConverToInt(data[j][0]) == yearTarget[0] && stringConverToInt(data[j][3]) == numTarget[0] && data[j][5] == data[i][5] && data[j][6] == data[i][6]){

                        sumPreDay[0] += stringConverToDouble(data[j][7]);

                        for(int p = 1; p < 7; p++){
                            sumPreDay[p] += stringConverToDouble(data[j][p + 9]);
                        }
                        countPreDay++;
                    }
                }
            }
        }
        //cout<<i<<endl;
        if(countPreDay > 0){
            for(j = 0; j < 7; j++){
                lineResult.push_back(doubleConverToString(sumPreDay[j]/countPreDay));
            }
        }
        else{
            for(j = 0; j < 7; j++){
                lineResult.push_back("0");
            }
        }
        
        if(count > 0){
            for(j = 0; j < count; j++){
                if(j == 0){
                    sumTmp = stringConverToDouble(data[id[j]][7]);
                }
                else{
                    tmp = stringConverToDouble(data[id[j]][7]);
                    sumTmp += tmp;
                }
            }      
        }
        else{
            sumTmp = 0;
        }
            
        if(count != 0){
            lineResult.push_back(doubleConverToString(sumTmp/count));
        }
        else{
            lineResult.push_back(doubleConverToString(sumTmp));
        }
        
        for(int p = 10; p < 17; p++){
            if(p != 15){
                if(count > 0){
                    for(j = 0; j < count; j++){
                        if(j == 0){
                            sumTmp = stringConverToDouble(data[id[j]][p]);
                        }
                        else{
                            tmp = stringConverToDouble(data[id[j]][p]);
                            sumTmp += tmp;
                        }
                    }
                    
                    
                }
                else{
                    sumTmp = 0;
                }

                
                if(count != 0){
                    lineResult.push_back(doubleConverToString(sumTmp/count));
                }
                else{
                    lineResult.push_back(doubleConverToString(sumTmp));
                }
            }
            
        }

        
        //result
        lineResult.push_back(data[i][8]);

        result.push_back(lineResult);
    }

    ofstream outFile;  
    outFile.open("output.csv", ios::out); 
    for(i = 1144; i < result.size(); i++){
        for(j = 0; j < result[i].size(); j++){
            
            if(j == (result[i].size()-1)){
                outFile<<result[i][j]<<endl;
            }
            else{
                outFile<<result[i][j]<<',';
            }
        }
        //outFile<<endl;
        //outFile<<result[i]<<endl;
    }
    outFile.close();  

    
    return 0;  
}  

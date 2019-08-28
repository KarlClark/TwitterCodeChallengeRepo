package com.twitter.challenge.data;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.lifecycle.MutableLiveData;

import com.twitter.challenge.StandardDeviationCalculator;
import com.twitter.challenge.TemperatureConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/* This class downloads and stores weather data from internet. Updates LiveData fields indicating
 * download status and errors.  Provides numerous getters for specific data needed for the app.
 * In case of an error it can be restarted at the point of the error.
  */
class Repository {

    private WeatherDataEndpointApi mEndpointApi;
    private List<WeatherData> mWeatherDataList = new ArrayList<>();
    private float mStandardDeviationC = -1.0f;
    private float mStandardDeviationF = -1.0f;
    private final MutableLiveData<Boolean> mCurrentDayLoaded = new MutableLiveData<>();
    private final MutableLiveData<Integer> mFutureDaysCount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHaveError = new MutableLiveData<>();
    private String mErrorMessage;
    private HandlerThread mLoadHandlerThread;
    private List<Float> mTempsC = new ArrayList<>();
    private List<Float> mTempsF = new ArrayList<>();
    private static final String CURRENT_ENDPOINT = "current";
    private static final String FUTURE_ENDPOINT = "future_";
    private static final int FUTURE_DAYS = 5;
    private static final String BASE_URL =  "https://twitter-code-challenge.s3.amazonaws.com/";

    /* Build Retrofit object and create Retrofit api class.  Initialize LiveData fields.  Note:
       this will notify any observers.  Then start data download.
    */
    Repository () {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        mEndpointApi = retrofit.create(WeatherDataEndpointApi.class);

        mHaveError.setValue(false);
        mFutureDaysCount.setValue(0);
        mCurrentDayLoaded.setValue(false);

        loadData(0);
    }

    //Check how much data has been downloaded and start new download at next endpoint.
    void restartLoad(){

        Boolean currentDayLoaded = mCurrentDayLoaded.getValue();
        boolean currentDayLoadedValue = false;
        if (currentDayLoaded != null){
            currentDayLoadedValue = currentDayLoaded;
        }

        Integer futureDaysCount = mFutureDaysCount.getValue();
        int futureDaysCountValue = 0;
        if (futureDaysCount != null){
            futureDaysCountValue = futureDaysCount;
        }
        if (! currentDayLoadedValue){
            loadData(0);
        } else {
            loadData(futureDaysCountValue + 1);
        }
    }

    // Start a HandlerThread and post a loading task starting at the specified by the
    // startPoint argument.  Note: HandlerThread will stop itself when it is done.
    private void loadData(int startPoint) {
        mLoadHandlerThread = new HandlerThread("Loader Thread");
        mLoadHandlerThread.start();
        Handler handler = new Handler(mLoadHandlerThread.getLooper());
        handler.post(() -> loadTask(startPoint));
    }

    /*Starting at the specified endpoint download the data for each endpoint.  Stop if any load
      gets an error. Update LiveData when loads are completed.  Make a list of the temperature
      data from each day so we can calculate the temperature standard deviation.  We calculate
      the standard deviation here so it will be available by the time any LiveData observers are
      notified of the last load.  Note: this method is run on a background thread.
    */
    private void loadTask(int startPoint) {

        for (int day = startPoint; day <= FUTURE_DAYS; day++){
            if (loadEndpoint(day) < 0){
                break;
            }
            if (day > 0) {
                mTempsC.add(day - 1, (float) getTemperature(day));
                mTempsF.add(day - 1, TemperatureConverter.celsiusToFahrenheit((float) getTemperature(day)));
                if (day == FUTURE_DAYS - 1) {
                    mStandardDeviationC = StandardDeviationCalculator.standardDeviation(mTempsC);
                    mStandardDeviationF = StandardDeviationCalculator.standardDeviation(mTempsF);
                }
            }
            postResults(day);
        }
        mLoadHandlerThread.quit();
    }

    /*Download the data for one endpoint (day). Update LiveData in case of errors. Store data from
      successful dowloads.
    */
    private int loadEndpoint(int day){

        String endPoint = getEndPoint(day);

        Call<WeatherData> call = mEndpointApi.getWeatherData(endPoint);
        Response<WeatherData> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            postError(e.getMessage());
            return -1;
        }

        ResponseBody errorBody = response.errorBody();

        // The errorBody is actually a read once stream, and reading it may actually produce
        // its own error.
        if (errorBody != null) {
            try {
                postError(errorBody.string());
                errorBody.close();
                return -1;

            } catch (IOException e) {
                postError(e.getMessage());
                errorBody.close();
                return -1;
            }
        }

        mWeatherDataList.add(day, response.body());
        return 1;
    }

    // Store error message and notify LiveData observers.
    private void postError(String errorMsg){
        mErrorMessage = errorMsg;
        mHaveError.postValue(true);
    }

    // Notify LiveData observers of successful downloads.
    private void postResults(int day) {

        Boolean haveError = mHaveError.getValue();
        boolean haveErrorValue = false;
        if (haveError != null){
            haveErrorValue = haveError;
        }

        //Reset error flag if need be, but don't do it every time so LiveData observers aren't
        //repeatedly notified of no-error conditions.
        if (haveErrorValue) {
            mHaveError.postValue(false);
        }

        if (day == 0) {
            mCurrentDayLoaded.postValue(true);
        } else {
            mFutureDaysCount.postValue(day);
        }
    }

    // Return the endpoint string for the URI.  Looks like either "current" or "future_n" where
    // n is the number of days in the future.
    private String getEndPoint(int day) {
        if (day == 0) {
            return CURRENT_ENDPOINT;
        } else {
            return FUTURE_ENDPOINT + day;
        }
    }

    MutableLiveData<Boolean> getCurrentDayLoaded() {
        return mCurrentDayLoaded;
    }

    MutableLiveData<Integer> getDataCount() {
        return mFutureDaysCount;
    }

    MutableLiveData<Boolean> haveError() {
        return mHaveError;
    }

    String getErrorMessage() {
        return mErrorMessage;
    }

    String getName(int day) {
        if (day >= mWeatherDataList.size()){
            return "";
        }
        return mWeatherDataList.get(day).getName();
    }

    double getTemperature(int day){
        if (day >= mWeatherDataList.size()){
            return -1000L;
        }
        return mWeatherDataList.get(day).getWeather().getTemp();
    }

    double getWindSpeed(int day){
        if (day >= mWeatherDataList.size()){
            return -1L;
        }
        return mWeatherDataList.get(day).getWind().getSpeed();
    }

    int getCloudiness (int day){
        if (day >= mWeatherDataList.size()){
            return -1;
        }
        return mWeatherDataList.get(day).getClouds().getCloudiness();
    }

    float getStandardDeviationCelsius(){
        return mStandardDeviationC;
    }

    float getStandardDeviationFahrenheit() {
        return mStandardDeviationF;
    }

    int getFutureDays() {
        return FUTURE_DAYS;
    }
}

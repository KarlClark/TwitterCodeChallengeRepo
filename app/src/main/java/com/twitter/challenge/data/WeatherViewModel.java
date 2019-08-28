package com.twitter.challenge.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

/*
 * ViewModel that the activity can use to observe data in the repository.  Has getters for the
 * various LiveData fields and for numerous other data values needed for the app.
 */

public class WeatherViewModel extends ViewModel {

    private Repository mRepository = new Repository();

    public LiveData<Boolean> getLiveCurrentDayLoaded() {
        return mRepository.getCurrentDayLoaded();
    }

    public LiveData<Integer> getLiveDataCount() {
        return mRepository.getDataCount();
    }

    public LiveData<Boolean> getLiveHaveError() {
        return mRepository.haveError();
    }

    public boolean currentDayLoaded() {
        Boolean value = mRepository.getCurrentDayLoaded().getValue();
        if (value != null){
            return value;
        }
        return false;
    }

    public int getFutureDaysLoaded() {
        Integer value = mRepository.getDataCount().getValue();
        if (value != null){
            return value;
        }
        return 0;
    }

    public boolean haveError(){
        Boolean value = mRepository.haveError().getValue();
        if (value != null){
            return value;
        }
        return false;
    }

    public void restartLoad(){
        mRepository.restartLoad();
    }

    public String getErrorMessage(){
        return mRepository.getErrorMessage();
    }

    public String getName(int day){
        return mRepository.getName(day);
    }

    public float getTemperature(int day){
        return (float)mRepository.getTemperature(day);
    }

    public double getWindSpeed(int day){
        return mRepository.getWindSpeed(day);
    }

    public int getCloudiness (int day){
        return mRepository.getCloudiness(day);
    }

    public float getStandardDeviationCelsius(){
        return mRepository.getStandardDeviationCelsius();
    }

    public float getStandardDeviationFahrenheit() {
        return mRepository.getStandardDeviationFahrenheit();
    }

    public int getFutureDays(){
        return mRepository.getFutureDays();
    }
}

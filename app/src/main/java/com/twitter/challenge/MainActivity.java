package com.twitter.challenge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.twitter.challenge.data.WeatherViewModel;

/**
 * This app has one activity, and no fragments.  Displays weather data for the current day.  If
 * requested (button press) displays the standard deviation of temperature for the next five days.
 * Displays progress bar while downloading data.  Displays error messages from failed downloads.
 * Observers LiveData from a ViewModel to monitor when data is available and when there are errors.
 */

public class MainActivity extends AppCompatActivity {

    private WeatherViewModel mWeatherViewModel;
    private TextView mTvName;
    private TextView mTvWind;
    private TextView mTvTemperature;
    private ImageView mIvCloudiness;
    private ProgressBar mPbLoading;
    private boolean mWaitingForFutureDays = false;
    private AlertDialog mDeviationDialog;
    private AlertDialog mErrorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvName = findViewById(R.id.tvName);
        mTvWind = findViewById(R.id.tvWind);
        mTvTemperature = findViewById(R.id.tvTemperature);
        mIvCloudiness = findViewById(R.id.ivCloudiness);
        mPbLoading = findViewById(R.id.pbLoading);
        Button btnDeviation = findViewById(R.id.btnDeviation);
        btnDeviation.setOnClickListener(v -> displayDeviation());

        setUpObservers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mErrorDialog != null && mErrorDialog.isShowing()){
            mErrorDialog.dismiss();
        }

        if (mDeviationDialog != null && mDeviationDialog.isShowing()){
            mDeviationDialog.dismiss();
        }
    }

    // Create a ViewModel object and observers.
    private void setUpObservers(){

        mWeatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);

        final Observer<Boolean> currentDayLoadedObserver = (dataLoaded -> {
            if (dataLoaded){
                processData();
            }
        });

        final Observer<Integer> futureDaysObserver = (dataCount -> {
            if (dataCount == mWeatherViewModel.getFutureDays() && mWaitingForFutureDays){
                mPbLoading.setVisibility(View.INVISIBLE);
                mWaitingForFutureDays = false;
                displayDeviation();
            }
        });

        final Observer<Boolean> haveErrorObserver = (this :: processError);

        mWeatherViewModel.getLiveCurrentDayLoaded().observe(this, currentDayLoadedObserver);
        mWeatherViewModel.getLiveDataCount().observe(this, futureDaysObserver);
        mWeatherViewModel.getLiveHaveError().observe(this, haveErrorObserver);
    }

    // Called when we are notified that the current days data has been downloaded.  Fill
    // in the UI.
    private void processData() {

        // Leave progress bar on if user pressed standard deviation button before we got here.
        if ( ! mWaitingForFutureDays) {
            mPbLoading.setVisibility(View.INVISIBLE);
        }
        int currentDay = 0;
        mTvName.setText(getString(R.string.location, mWeatherViewModel.getName(currentDay)));
        float tempC = mWeatherViewModel.getTemperature(currentDay);
        float tempF = TemperatureConverter.celsiusToFahrenheit(tempC);
        mTvTemperature.setText(getString(R.string.temperature, tempC, tempF));
        mTvWind.setText(getString(R.string.wind, mWeatherViewModel.getWindSpeed(currentDay)));
        if (mWeatherViewModel.getCloudiness(currentDay) > 50) {
            mIvCloudiness.setVisibility(View.VISIBLE);
        } else {
            mIvCloudiness.setVisibility(View.INVISIBLE);
        }
    }

    // Notified that there was an error.  If the error happened while downloading the currents days
    // data then display error right away.  Otherwise there is no need unless the user actually
    // requests the standard deviation which requires the rest of the data.
    private void processError(boolean haveError) {
        if (haveError) {
            if ( ! mWeatherViewModel.currentDayLoaded()) {
                displayError();
            }
        }
    }

    // Display the standard deviation.  If data is not available yet, then set the
    // mWaitingForFutureDays flag  and start the progress bar.  If there is already and error, then
    // display that and return.
    private void displayDeviation() {

        if (mWaitingForFutureDays){
            return;
        }

        mWaitingForFutureDays = true;

        if (mWeatherViewModel.haveError()){
            displayError();
            return;
        }

        if (mWeatherViewModel.getFutureDaysLoaded() != mWeatherViewModel.getFutureDays()){
            mPbLoading.setVisibility(View.VISIBLE);
            return;
        }

        mWaitingForFutureDays = false;

        float tempC = mWeatherViewModel.getStandardDeviationCelsius();
        float tempF = mWeatherViewModel.getStandardDeviationFahrenheit();

        mDeviationDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_deviation, mWeatherViewModel.getFutureDays()))
                .setMessage(getString(R.string.temperature, tempC, tempF))
                .setNeutralButton(R.string.ok, (dialog, id) -> {})
                .show();
    }

    // Display an error message, and ask the user if he wants to retry the download.  If hes
    // says yes, then restart the download. If he says no and no data at all has been downloaded
    // the finish the app.  Else just dismiss the error dialog.
    private void displayError(){

        mPbLoading.setVisibility(View.INVISIBLE);
        mErrorDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(getString(R.string.error_detail, mWeatherViewModel.getErrorMessage()))
                .setCancelable(false)
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    mWaitingForFutureDays = false;
                    if ( ! mWeatherViewModel.currentDayLoaded()) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    mPbLoading.setVisibility(View.VISIBLE);
                    mWeatherViewModel.restartLoad();
                })
                .show();
    }
}

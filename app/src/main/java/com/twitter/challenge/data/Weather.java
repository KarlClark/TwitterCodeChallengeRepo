
package com.twitter.challenge.data;

import com.squareup.moshi.Json;

class Weather {

    @Json(name = "temp")
    private Double temp;
    @Json(name = "pressure")
    private Integer pressure;
    @Json(name = "humidity")
    private Integer humidity;

    Double getTemp() {
        return temp;
    }

    void setTemp(Double temp) {
        this.temp = temp;
    }

    Integer getPressure() {
        return pressure;
    }

    void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    Integer getHumidity() {
        return humidity;
    }

    void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

}

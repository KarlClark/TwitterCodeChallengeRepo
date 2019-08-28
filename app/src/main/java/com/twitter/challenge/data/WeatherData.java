
package com.twitter.challenge.data;

import com.squareup.moshi.Json;

class WeatherData {

    @Json(name = "coord")
    private Coord coord;
    @Json(name = "weather")
    private Weather weather;
    @Json(name = "wind")
    private Wind wind;
    @Json(name = "rain")
    private Rain rain;
    @Json(name = "clouds")
    private Clouds clouds;
    @Json(name = "name")
    private String name;

    Coord getCoord() {
        return coord;
    }

    void setCoord(Coord coord) {
        this.coord = coord;
    }

    Weather getWeather() {
        return weather;
    }

    void setWeather(Weather weather) {
        this.weather = weather;
    }

    Wind getWind() {
        return wind;
    }

    void setWind(Wind wind) {
        this.wind = wind;
    }

    Rain getRain() {
        return rain;
    }

    void setRain(Rain rain) {
        this.rain = rain;
    }

    Clouds getClouds() {
        return clouds;
    }

    void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

}

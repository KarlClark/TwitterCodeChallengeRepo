
package com.twitter.challenge.data;

import com.squareup.moshi.Json;

class Coord {

    @Json(name = "lon")
    private Double lon;
    @Json(name = "lat")
    private Double lat;

     Double getLon() {
        return lon;
    }

    void setLon(Double lon) {
        this.lon = lon;
    }

    Double getLat() {
        return lat;
    }

    void setLat(Double lat) {
        this.lat = lat;
    }

}

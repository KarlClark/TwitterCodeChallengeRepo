
package com.twitter.challenge.data;

import com.squareup.moshi.Json;

class Wind {

    @Json(name = "speed")
    private Double speed;
    @Json(name = "deg")
    private Integer deg;

    Double getSpeed() {
        return speed;
    }

    void setSpeed(Double speed) {
        this.speed = speed;
    }

    Integer getDeg() {
        return deg;
    }

    void setDeg(Integer deg) {
        this.deg = deg;
    }

}

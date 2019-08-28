
package com.twitter.challenge.data;

import com.squareup.moshi.Json;

class Clouds {

    @Json(name = "cloudiness")
    private Integer cloudiness;

    Integer getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(Integer cloudiness) {
        this.cloudiness = cloudiness;
    }

}

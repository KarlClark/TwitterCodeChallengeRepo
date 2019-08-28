
package com.twitter.challenge.data;

import com.squareup.moshi.Json;

class Rain {

    @Json(name = "3h")
    private Integer _3h;

    Integer get3h() {
        return _3h;
    }

    void set3h(Integer _3h) {
        this._3h = _3h;
    }

}

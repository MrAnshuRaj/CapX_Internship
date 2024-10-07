package com.capx.investing;

import com.google.gson.annotations.SerializedName;

public class ApiNinjasResponse {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}

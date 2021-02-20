package com.mobius.callbreakandroid.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ChooseServerService {
    @GET("chooseServer")
    Call<ResponseBody> getChooseServiceResponse(@QueryMap Map<String, String> parameter);
}

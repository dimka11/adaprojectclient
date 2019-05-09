package com.ds.da.accelerationdata;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

interface ApiConfig {
    @Multipart
    @POST("{fileupload}")
    Call<ServerResponse> uploadFile(@Path("fileupload") String value, @Part MultipartBody.Part file,
                                    @Part("file") RequestBody name);
}

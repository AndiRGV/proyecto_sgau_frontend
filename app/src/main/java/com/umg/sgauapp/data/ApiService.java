package com.umg.sgauapp.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {
    @GET Call<JsonElement> listar(@Url String url);
    @GET Call<JsonElement> obtener(@Url String url);
    @POST Call<JsonElement> crear(@Url String url, @Body JsonObject body);
    @PUT Call<JsonElement> actualizar(@Url String url, @Body JsonObject body);
    @DELETE Call<JsonElement> eliminar(@Url String url);
}

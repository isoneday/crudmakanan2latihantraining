package com.training.crudmakananapp.network;

import com.training.crudmakananapp.model.ModelUser;
import com.training.crudmakananapp.model.ResponseDataMakanan;
import com.training.crudmakananapp.model.ResponseKategorimakan;
import com.training.crudmakananapp.model.ResponseRegister;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestApi {


    //endpoint untuk register
    @FormUrlEncoded
    @POST("registeruser.php")
    Call<ResponseRegister> registeruser(
         @Field("vsnama") String nama,
         @Field("vsalamat") String alamat,
         @Field("vsjenkel") String jenkel,
         @Field("vsnotelp") String notelp,
         @Field("vsusername") String username,
         @Field("vslevel") String level,
  @Field("vspassword") String password);

    //

    @FormUrlEncoded
    @POST("loginuser.php")
    Call<ModelUser> loginUser(
            @Field("edtusername") String strusername,
            @Field("edtpassword") String strpassword,
            @Field("vslevel") String strlevel
    );
    @FormUrlEncoded
    @POST("getdatamakanan.php")
    Call<ResponseDataMakanan> getdatamakanan(
            @Field("vsiduser") String iduser,
            @Field("vsidkastrkategorimakanan") String idkategori

    );
    @FormUrlEncoded
    @POST("deletedatamakanan.php")
    Call<ResponseRegister> deletedatamakanan(
            @Field("vsidmakanan") String idmakanan

    );

    @GET("kategorimakanan.php")
    Call<ResponseKategorimakan> getkategorimakanan();
}




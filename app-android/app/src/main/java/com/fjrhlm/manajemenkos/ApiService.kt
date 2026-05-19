package com.fjrhlm.manajemenkos

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // Login (POST)
    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // Register (POST)
    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("nama") nama: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    // Mengambil Tagihan (GET)
    @GET("tagihan/{id_user}")
    fun getTagihan(
        @Path("id_user") idUser: Int
    ): Call<TagihanResponse>

    // Mengirim Gambar Bukti Transfer (POST Multipart) - (Lama, Opsional)
    @Multipart
    @POST("tagihan/upload/{id_tagihan}")
    fun uploadBukti(
        @Path("id_tagihan") idTagihan: Int,
        @Part bukti_transfer: MultipartBody.Part
    ): Call<TagihanResponse>

    // Bayar menggunakan Midtrans (POST)
    @FormUrlEncoded
    @POST("tagihan/pay")
    fun payTagihan(
        @Field("id_tagihan") idTagihan: Int
    ): Call<MidtransResponse>

    // Profil (GET)
    @GET("profile/{id_user}")
    fun getProfile(
        @Path("id_user") idUser: Int
    ): Call<ProfileResponse>

    // Update Profil (PUT)
    @FormUrlEncoded
    @PUT("profile/{id_user}")
    fun updateProfile(
        @Path("id_user") idUser: Int,
        @Field("nama") nama: String,
        @Field("email") email: String
    ): Call<GenericResponse>

    // Kirim Keluhan (POST)
    @FormUrlEncoded
    @POST("keluhan")
    fun kirimKeluhan(
        @Field("id_user") idUser: Int,
        @Field("judul") judul: String,
        @Field("isi") isi: String
    ): Call<GenericResponse>

    // Ambil Riwayat Keluhan (GET)
    @GET("keluhan/{id_user}")
    fun getKeluhanUser(
        @Path("id_user") idUser: Int
    ): Call<KeluhanListResponse>

    // Hapus Keluhan (DELETE)
    @DELETE("keluhan/{id_keluhan}")
    fun hapusKeluhan(
        @Path("id_keluhan") idKeluhan: Int
    ): Call<GenericResponse>
}
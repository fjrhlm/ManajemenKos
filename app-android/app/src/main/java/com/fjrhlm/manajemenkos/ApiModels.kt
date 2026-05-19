package com.fjrhlm.manajemenkos

import com.google.gson.annotations.SerializedName

// Model untuk menangkap respon Login
data class LoginResponse(
    val status: String,
    val message: String,
    val data: UserModel?
)

data class UserModel(
    @SerializedName("id_user") val idUser: Int,
    val nama: String,
    val email: String,
    val role: String
)

// Model untuk menangkap respon Register
data class RegisterResponse(
    val status: String,
    val message: String,
    val data: UserModel?
)

// Model untuk menangkap respon Tagihan
data class TagihanResponse(
    val status: String,
    val message: String,
    val data: TagihanModel?
)

data class TagihanModel(
    @SerializedName("id_tagihan") val idTagihan: Int,
    @SerializedName("bulan_tahun") val bulanTahun: String,
    val nominal: String,
    @SerializedName("status_bayar") val statusBayar: String,
    val user: UserDetail?
)

data class UserDetail(
    val nama: String,
    val kamar: KamarDetail?
)

data class KamarDetail(
    @SerializedName("nomor_kamar") val nomorKamar: String
)

// Model untuk menangkap respon Profil
data class ProfileResponse(
    val status: String,
    val data: ProfileModel?
)

data class ProfileModel(
    @SerializedName("id_user") val idUser: Int,
    val nama: String,
    val email: String,
    val role: String,
    val kamar: KamarDetail?
)

// Model untuk menangkap respon Keluhan
data class KeluhanListResponse(
    val status: String,
    val data: List<KeluhanModel>?
)

data class KeluhanModel(
    @SerializedName("id_keluhan") val idKeluhan: Int,
    val judul: String,
    val isi: String,
    @SerializedName("created_at") val createdAt: String?
)

// Model respon umum (untuk update profil, kirim keluhan, dll)
data class GenericResponse(
    val status: String,
    val message: String
)

// Model untuk menangkap respon Midtrans Pay
data class MidtransResponse(
    val status: String,
    val token: String?,
    @SerializedName("redirect_url") val redirectUrl: String?,
    val message: String?
)
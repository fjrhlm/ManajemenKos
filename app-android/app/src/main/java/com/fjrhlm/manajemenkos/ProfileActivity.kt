package com.fjrhlm.manajemenkos

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val sharedPref = getSharedPreferences("KosApp", Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("ID_USER", 0)

        val tvBack = findViewById<TextView>(R.id.tvBack)
        val etNama = findViewById<EditText>(R.id.etNama)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etKamar = findViewById<EditText>(R.id.etKamar)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        tvBack.setOnClickListener { finish() }

        // Ambil data profil dari server
        ApiClient.instance.getProfile(idUser).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val data = response.body()?.data
                    etNama.setText(data?.nama ?: "")
                    etEmail.setText(data?.email ?: "")
                    etKamar.setText(data?.kamar?.nomorKamar ?: "Belum ada kamar")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
            }
        })

        // Tombol Simpan
        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (nama.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nama dan Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSimpan.text = "Menyimpan..."
            btnSimpan.isEnabled = false

            ApiClient.instance.updateProfile(idUser, nama, email).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    btnSimpan.text = "SIMPAN PERUBAHAN"
                    btnSimpan.isEnabled = true

                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@ProfileActivity, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    } else {
                        val msg = response.body()?.message ?: "Gagal menyimpan"
                        Toast.makeText(this@ProfileActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    btnSimpan.text = "SIMPAN PERUBAHAN"
                    btnSimpan.isEnabled = true
                    Toast.makeText(this@ProfileActivity, "Koneksi Error!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

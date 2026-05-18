package com.fjrhlm.manajemenkos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("KosApp", Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("ID_USER", 0)

        if (idUser == 0) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Ambil data tagihan
        fetchDataTagihan(idUser)

        // Menu Profil
        val cardProfil = findViewById<CardView>(R.id.cardProfil)
        cardProfil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Menu Keluhan
        val cardKeluhan = findViewById<CardView>(R.id.cardKeluhan)
        cardKeluhan.setOnClickListener {
            startActivity(Intent(this, KeluhanActivity::class.java))
        }

        // Tombol Logout
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchDataTagihan(idUser: Int) {
        val tvNama = findViewById<TextView>(R.id.tvNama)
        val tvKamar = findViewById<TextView>(R.id.tvKamar)
        val tvTagihanBulan = findViewById<TextView>(R.id.tvTagihanBulan)
        val tvNominal = findViewById<TextView>(R.id.tvNominal)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        ApiClient.instance.getTagihan(idUser).enqueue(object : Callback<TagihanResponse> {

            override fun onResponse(call: Call<TagihanResponse>, response: Response<TagihanResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val tagihan = response.body()?.data
                    val userDetail = tagihan?.user

                    tvNama.text = "Halo, ${userDetail?.nama}!"
                    tvKamar.text = "Kamar ${userDetail?.kamar?.nomorKamar ?: "-"}"
                    tvTagihanBulan.text = "Tagihan Bulan Ini (${tagihan?.bulanTahun})"

                    val nominalAsli = tagihan?.nominal?.toDouble()?.toInt() ?: 0
                    val nominalRupiah = "Rp " + String.format("%,d", nominalAsli).replace(",", ".")
                    tvNominal.text = nominalRupiah

                    tvStatus.text = "Status: ${tagihan?.statusBayar}"

                    // Warna status
                    if (tagihan?.statusBayar == "Lunas" || tagihan?.statusBayar == "LUNAS") {
                        tvStatus.setTextColor(resources.getColor(R.color.success, null))
                        tvNominal.setTextColor(resources.getColor(R.color.success, null))
                    } else {
                        tvStatus.setTextColor(resources.getColor(R.color.danger, null))
                    }

                    // Tombol Bayar
                    val btnBayar = findViewById<Button>(R.id.btnBayar)
                    btnBayar.setOnClickListener {
                        val intent = Intent(this@MainActivity, PaymentActivity::class.java)
                        intent.putExtra("ID_TAGIHAN", tagihan?.idTagihan)
                        startActivity(intent)
                    }

                } else {
                    tvTagihanBulan.text = "Tagihan Bulan Ini"
                    tvNominal.text = "Rp -"
                    tvStatus.text = "Belum ada tagihan"

                    // Tetap tampilkan nama dari profil
                    ApiClient.instance.getProfile(idUser).enqueue(object : Callback<ProfileResponse> {
                        override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                            if (response.isSuccessful) {
                                val data = response.body()?.data
                                tvNama.text = "Halo, ${data?.nama}!"
                                tvKamar.text = "Kamar ${data?.kamar?.nomorKamar ?: "-"}"
                            }
                        }
                        override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {}
                    })
                }
            }

            override fun onFailure(call: Call<TagihanResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
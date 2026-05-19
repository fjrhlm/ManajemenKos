package com.fjrhlm.manajemenkos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

    private var idTagihan: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Ambil ID Tagihan yang dilempar dari MainActivity
        idTagihan = intent.getIntExtra("ID_TAGIHAN", 0)

        val btnSubmit = findViewById<Button>(R.id.btnSubmitPayment)
        val tvBack = findViewById<TextView>(R.id.tvBack)

        tvBack.setOnClickListener { finish() }

        btnSubmit.setOnClickListener {
            btnSubmit.text = "Memproses..."
            btnSubmit.isEnabled = false

            // Minta URL Midtrans dari server
            ApiClient.instance.payTagihan(idTagihan).enqueue(object : Callback<MidtransResponse> {
                override fun onResponse(call: Call<MidtransResponse>, response: Response<MidtransResponse>) {
                    btnSubmit.text = "Lanjutkan Pembayaran"
                    btnSubmit.isEnabled = true

                    if (response.isSuccessful && response.body()?.status == "success") {
                        val redirectUrl = response.body()?.redirectUrl
                        if (redirectUrl != null) {
                            // Buka halaman pembayaran Midtrans di Browser HP
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
                            startActivity(browserIntent)
                            finish() // Tutup halaman ini, biarkan user selesaikan di browser
                        }
                    } else {
                        val errMsg = response.errorBody()?.string() ?: response.body()?.message ?: "Gagal memproses"
                        Toast.makeText(this@PaymentActivity, "Gagal: $errMsg", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<MidtransResponse>, t: Throwable) {
                    btnSubmit.text = "Lanjutkan Pembayaran"
                    btnSubmit.isEnabled = true
                    Toast.makeText(this@PaymentActivity, "Koneksi Error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
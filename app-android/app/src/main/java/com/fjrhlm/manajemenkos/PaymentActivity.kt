package com.fjrhlm.manajemenkos

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val tvBack = findViewById<TextView>(R.id.tvBack)
        val btnSubmitPayment = findViewById<Button>(R.id.btnSubmitPayment)

        // Fungsi tombol kembali
        tvBack.setOnClickListener {
            finish() // Menutup halaman ini dan kembali ke Dashboard
        }

        // Fungsi tombol kirim bukti (Sementara pakai Toast dulu)
        btnSubmitPayment.setOnClickListener {
            // Nanti di sini kita pasang logika Retrofit untuk nge-POST data (Create) ke API
            Toast.makeText(this, "Bukti pembayaran berhasil dikirim untuk divalidasi Ibu Kos!", Toast.LENGTH_LONG).show()
            finish() // Kembali ke dashboard setelah "berhasil" kirim
        }
    }
}
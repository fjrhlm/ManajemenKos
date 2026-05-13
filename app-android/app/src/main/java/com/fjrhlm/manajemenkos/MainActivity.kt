package com.fjrhlm.manajemenkos

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBayar = findViewById<Button>(R.id.btnBayar)

        btnBayar.setOnClickListener {
            // Nantinya ini akan pindah ke PaymentActivity untuk upload bukti (CRUD: Create)
            Toast.makeText(this, "Menuju Halaman Pembayaran...", Toast.LENGTH_SHORT).show()
        }
    }
}
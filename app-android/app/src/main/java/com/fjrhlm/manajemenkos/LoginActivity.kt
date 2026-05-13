package com.fjrhlm.manajemenkos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Mengenalkan komponen XML ke Kotlin
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Memberikan aksi saat tombol MASUK diklik
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            // Validasi sederhana (jangan biarkan kosong)
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                // Logic sementara: Langsung pindah ke MainActivity (Dashboard)
                // Nanti di sini kita pasang Retrofit/Volley untuk ngecek ke Database API
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                // Perintah untuk pindah halaman (Intent)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                // Menutup LoginActivity agar user tidak bisa kembali ke halaman login pakai tombol 'Back' HP
                finish()
            }
        }
    }
}
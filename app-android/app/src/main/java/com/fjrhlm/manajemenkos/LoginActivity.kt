package com.fjrhlm.manajemenkos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek apakah sudah login sebelumnya
        val sharedPref = getSharedPreferences("KosApp", Context.MODE_PRIVATE)
        val savedId = sharedPref.getInt("ID_USER", 0)
        if (savedId != 0) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // Tombol Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.text = "Loading..."
            btnLogin.isEnabled = false

            ApiClient.instance.loginUser(email, password).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    btnLogin.text = "MASUK"
                    btnLogin.isEnabled = true

                    if (response.isSuccessful && response.body()?.status == "success") {
                        val user = response.body()?.data
                        Toast.makeText(this@LoginActivity, "Halo, ${user?.nama}!", Toast.LENGTH_SHORT).show()

                        with(sharedPref.edit()) {
                            putInt("ID_USER", user?.idUser ?: 0)
                            apply()
                        }

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Gagal: Email/Password salah", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    btnLogin.text = "MASUK"
                    btnLogin.isEnabled = true
                    Toast.makeText(this@LoginActivity, "Koneksi Error: Periksa server Laravel!", Toast.LENGTH_LONG).show()
                }
            })
        }

        // Link ke halaman Register
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
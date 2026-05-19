package com.fjrhlm.manajemenkos

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnRegister.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 4) {
                Toast.makeText(this, "Password minimal 4 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.text = "Loading..."
            btnRegister.isEnabled = false

            ApiClient.instance.registerUser(nama, email, password).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    btnRegister.text = "Daftar Sekarang"
                    btnRegister.isEnabled = true

                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Registrasi gagal"
                        Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    btnRegister.text = "Daftar Sekarang"
                    btnRegister.isEnabled = true
                    Toast.makeText(this@RegisterActivity, "Koneksi Error!", Toast.LENGTH_LONG).show()
                }
            })
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }
}
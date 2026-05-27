package com.fjrhlm.manajemenkos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("KosApp", Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("ID_USER", 0)

        val etNama = view.findViewById<EditText>(R.id.etNama)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etKamar = view.findViewById<EditText>(R.id.etKamar)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Ambil data profil dari server
        ApiClient.instance.getProfile(idUser).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (!isAdded) return
                if (response.isSuccessful && response.body()?.status == "success") {
                    val data = response.body()?.data
                    etNama.setText(data?.nama ?: "")
                    etEmail.setText(data?.email ?: "")
                    etKamar.setText(data?.kamar?.nomorKamar ?: "Belum ada kamar")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(requireContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show()
            }
        })

        // Tombol Simpan
        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (nama.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Nama dan Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSimpan.text = "Menyimpan..."
            btnSimpan.isEnabled = false

            ApiClient.instance.updateProfile(idUser, nama, email).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (!isAdded) return
                    btnSimpan.text = "Simpan Perubahan"
                    btnSimpan.isEnabled = true

                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(requireContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    } else {
                        val msg = response.body()?.message ?: "Gagal menyimpan"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    if (!isAdded) return
                    btnSimpan.text = "Simpan Perubahan"
                    btnSimpan.isEnabled = true
                    Toast.makeText(requireContext(), "Koneksi Error!", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Tombol Keluar Akun
        btnLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}

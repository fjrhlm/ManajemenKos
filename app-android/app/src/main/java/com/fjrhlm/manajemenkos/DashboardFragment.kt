package com.fjrhlm.manajemenkos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("KosApp", Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("ID_USER", 0)

        if (idUser == 0) return

        fetchDataTagihan(view, idUser)
    }

    override fun onResume() {
        super.onResume()
        // Refresh data setiap kali fragment ditampilkan kembali (misal setelah bayar)
        val sharedPref = requireContext().getSharedPreferences("KosApp", Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("ID_USER", 0)
        if (idUser != 0) {
            fetchDataTagihan(requireView(), idUser)
        }
    }

    private fun fetchDataTagihan(view: View, idUser: Int) {
        val tvNama = view.findViewById<TextView>(R.id.tvNama)
        val tvKamar = view.findViewById<TextView>(R.id.tvKamar)
        val tvTagihanBulan = view.findViewById<TextView>(R.id.tvTagihanBulan)
        val tvNominal = view.findViewById<TextView>(R.id.tvNominal)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        val tvInfoKamar = view.findViewById<TextView>(R.id.tvInfoKamar)
        val tvInfoPeriode = view.findViewById<TextView>(R.id.tvInfoPeriode)

        ApiClient.instance.getTagihan(idUser).enqueue(object : Callback<TagihanResponse> {

            override fun onResponse(call: Call<TagihanResponse>, response: Response<TagihanResponse>) {
                if (!isAdded) return // Cegah crash jika fragment sudah di-detach

                if (response.isSuccessful && response.body()?.status == "success") {
                    val tagihan = response.body()?.data
                    val userDetail = tagihan?.user

                    tvNama.text = "Halo, ${userDetail?.nama}!"
                    tvKamar.text = "Kamar ${userDetail?.kamar?.nomorKamar ?: "-"}"
                    tvTagihanBulan.text = "Tagihan Bulan Ini (${tagihan?.bulanTahun})"
                    tvInfoKamar.text = "Kamar ${userDetail?.kamar?.nomorKamar ?: "-"}"
                    tvInfoPeriode.text = tagihan?.bulanTahun ?: "-"

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
                    val btnBayar = view.findViewById<Button>(R.id.btnBayar)
                    btnBayar.setOnClickListener {
                        val intent = Intent(requireContext(), PaymentActivity::class.java)
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
                            if (!isAdded) return
                            if (response.isSuccessful) {
                                val data = response.body()?.data
                                tvNama.text = "Halo, ${data?.nama}!"
                                tvKamar.text = "Kamar ${data?.kamar?.nomorKamar ?: "-"}"
                                tvInfoKamar.text = "Kamar ${data?.kamar?.nomorKamar ?: "-"}"
                            }
                        }
                        override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {}
                    })
                }
            }

            override fun onFailure(call: Call<TagihanResponse>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(requireContext(), "Gagal terhubung: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

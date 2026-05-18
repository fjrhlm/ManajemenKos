package com.fjrhlm.manajemenkos

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KeluhanActivity : AppCompatActivity() {

    private val keluhanList = mutableListOf<KeluhanModel>()
    private lateinit var adapter: KeluhanAdapter
    private var idUser = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keluhan)

        val sharedPref = getSharedPreferences("KosApp", Context.MODE_PRIVATE)
        idUser = sharedPref.getInt("ID_USER", 0)

        val tvBack = findViewById<TextView>(R.id.tvBack)
        val etIsi = findViewById<EditText>(R.id.etIsi)
        val btnKirim = findViewById<Button>(R.id.btnKirim)
        val rvKeluhan = findViewById<RecyclerView>(R.id.rvKeluhan)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)

        tvBack.setOnClickListener { finish() }

        // Setup RecyclerView
        adapter = KeluhanAdapter(keluhanList) { keluhan ->
            // Long press => konfirmasi hapus
            AlertDialog.Builder(this)
                .setTitle("Hapus Keluhan")
                .setMessage("Yakin ingin menghapus keluhan ini?")
                .setPositiveButton("Hapus") { _, _ ->
                    hapusKeluhan(keluhan.idKeluhan, rvKeluhan, tvEmpty)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        rvKeluhan.layoutManager = LinearLayoutManager(this)
        rvKeluhan.adapter = adapter

        // Load riwayat keluhan
        loadKeluhan(rvKeluhan, tvEmpty)

        // Kirim keluhan
        btnKirim.setOnClickListener {
            val isi = etIsi.text.toString().trim()

            if (isi.isEmpty()) {
                Toast.makeText(this, "Isi keluhan harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnKirim.text = "Mengirim..."
            btnKirim.isEnabled = false

            // Gunakan potongan isi sebagai judul default
            val judulDefault = if (isi.length > 20) isi.substring(0, 20) + "..." else isi

            ApiClient.instance.kirimKeluhan(idUser, judulDefault, isi).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    btnKirim.text = "KIRIM KELUHAN"
                    btnKirim.isEnabled = true

                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@KeluhanActivity, "Keluhan berhasil dikirim!", Toast.LENGTH_SHORT).show()
                        etIsi.text.clear()
                        loadKeluhan(rvKeluhan, tvEmpty)
                    } else {
                        Toast.makeText(this@KeluhanActivity, "Gagal mengirim keluhan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    btnKirim.text = "KIRIM KELUHAN"
                    btnKirim.isEnabled = true
                    Toast.makeText(this@KeluhanActivity, "Koneksi Error!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadKeluhan(rv: RecyclerView, tvEmpty: TextView) {
        ApiClient.instance.getKeluhanUser(idUser).enqueue(object : Callback<KeluhanListResponse> {
            override fun onResponse(call: Call<KeluhanListResponse>, response: Response<KeluhanListResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data ?: emptyList()
                    keluhanList.clear()
                    keluhanList.addAll(data)
                    adapter.notifyDataSetChanged()

                    if (data.isEmpty()) {
                        rv.visibility = View.GONE
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        rv.visibility = View.VISIBLE
                        tvEmpty.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<KeluhanListResponse>, t: Throwable) {
                Toast.makeText(this@KeluhanActivity, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hapusKeluhan(idKeluhan: Int, rv: RecyclerView, tvEmpty: TextView) {
        ApiClient.instance.hapusKeluhan(idKeluhan).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@KeluhanActivity, "Keluhan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadKeluhan(rv, tvEmpty)
                } else {
                    Toast.makeText(this@KeluhanActivity, "Gagal menghapus keluhan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@KeluhanActivity, "Koneksi Error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Adapter RecyclerView
    class KeluhanAdapter(
        private val items: List<KeluhanModel>,
        private val onLongClick: (KeluhanModel) -> Unit
    ) : RecyclerView.Adapter<KeluhanAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvJudul: TextView = view.findViewById(R.id.tvJudul)
            val tvIsi: TextView = view.findViewById(R.id.tvIsi)
            val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_keluhan, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvJudul.text = item.judul
            holder.tvIsi.text = item.isi
            holder.tvTanggal.text = item.createdAt ?: "-"

            // Long press untuk hapus
            holder.itemView.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }

        override fun getItemCount() = items.size
    }
}

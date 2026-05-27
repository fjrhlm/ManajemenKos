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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KeluhanFragment : Fragment() {

    private val keluhanList = mutableListOf<KeluhanModel>()
    private lateinit var adapter: KeluhanAdapter
    private var idUser = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_keluhan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("KosApp", Context.MODE_PRIVATE)
        idUser = sharedPref.getInt("ID_USER", 0)

        val etIsi = view.findViewById<EditText>(R.id.etIsi)
        val btnKirim = view.findViewById<Button>(R.id.btnKirim)
        val rvKeluhan = view.findViewById<RecyclerView>(R.id.rvKeluhan)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        // Setup RecyclerView
        adapter = KeluhanAdapter(keluhanList) { keluhan ->
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Keluhan")
                .setMessage("Yakin ingin menghapus keluhan ini?")
                .setPositiveButton("Hapus") { _, _ ->
                    hapusKeluhan(keluhan.idKeluhan, rvKeluhan, tvEmpty)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        rvKeluhan.layoutManager = LinearLayoutManager(requireContext())
        rvKeluhan.adapter = adapter

        // Load riwayat keluhan
        loadKeluhan(rvKeluhan, tvEmpty)

        // Kirim keluhan
        btnKirim.setOnClickListener {
            val isi = etIsi.text.toString().trim()

            if (isi.isEmpty()) {
                Toast.makeText(requireContext(), "Isi keluhan harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnKirim.text = "Mengirim..."
            btnKirim.isEnabled = false

            val judulDefault = if (isi.length > 20) isi.substring(0, 20) + "..." else isi

            ApiClient.instance.kirimKeluhan(idUser, judulDefault, isi).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (!isAdded) return
                    btnKirim.text = "Kirim Keluhan"
                    btnKirim.isEnabled = true

                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(requireContext(), "Keluhan berhasil dikirim!", Toast.LENGTH_SHORT).show()
                        etIsi.text.clear()
                        loadKeluhan(rvKeluhan, tvEmpty)
                    } else {
                        Toast.makeText(requireContext(), "Gagal mengirim keluhan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    if (!isAdded) return
                    btnKirim.text = "Kirim Keluhan"
                    btnKirim.isEnabled = true
                    Toast.makeText(requireContext(), "Koneksi Error!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadKeluhan(rv: RecyclerView, tvEmpty: TextView) {
        ApiClient.instance.getKeluhanUser(idUser).enqueue(object : Callback<KeluhanListResponse> {
            override fun onResponse(call: Call<KeluhanListResponse>, response: Response<KeluhanListResponse>) {
                if (!isAdded) return
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
                if (!isAdded) return
                Toast.makeText(requireContext(), "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hapusKeluhan(idKeluhan: Int, rv: RecyclerView, tvEmpty: TextView) {
        ApiClient.instance.hapusKeluhan(idKeluhan).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (!isAdded) return
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(requireContext(), "Keluhan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadKeluhan(rv, tvEmpty)
                } else {
                    Toast.makeText(requireContext(), "Gagal menghapus keluhan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(requireContext(), "Koneksi Error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Adapter RecyclerView (dipindahkan dari KeluhanActivity)
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

            holder.itemView.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }

        override fun getItemCount() = items.size
    }
}

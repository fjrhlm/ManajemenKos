package com.fjrhlm.manajemenkos

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class PaymentActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var idTagihan: Int = 0

    // Peluncur Galeri untuk memilih gambar
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri

            // Tampilkan gambar ke layar, sembunyikan teks
            findViewById<ImageView>(R.id.ivBukti).setImageURI(uri)
            findViewById<ImageView>(R.id.ivBukti).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvPilihFoto).visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Ambil ID Tagihan yang dilempar dari MainActivity
        idTagihan = intent.getIntExtra("ID_TAGIHAN", 0)

        val layUpload = findViewById<FrameLayout>(R.id.layUpload)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitPayment)
        val tvBack = findViewById<TextView>(R.id.tvBack)

        // Tombol Kembali
        tvBack.setOnClickListener { finish() }

        // Klik area foto untuk buka galeri HP
        layUpload.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Klik tombol Kirim
        btnSubmit.setOnClickListener {
            if (selectedImageUri == null) {
                Toast.makeText(this, "Pilih foto bukti transfer dulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ubah tampilan tombol saat loading
            btnSubmit.text = "Mengunggah..."
            btnSubmit.isEnabled = false

            uploadFileKeServer()
        }
    }

    private fun uploadFileKeServer() {
        // 1. Ubah URI dari galeri HP menjadi File nyata
        val file = uriToFile(selectedImageUri!!, this)

        // 2. Bungkus File ke dalam format Multipart agar bisa dikirim via Internet (Penulisan Versi 3)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("bukti_transfer", file.name, requestFile)

        // 3. Tembak ke API Laravel
        ApiClient.instance.uploadBukti(idTagihan, body).enqueue(object : Callback<TagihanResponse> {
            override fun onResponse(call: Call<TagihanResponse>, response: Response<TagihanResponse>) {
                val btnSubmit = findViewById<Button>(R.id.btnSubmitPayment)
                btnSubmit.text = "KIRIM BUKTI PEMBAYARAN"
                btnSubmit.isEnabled = true

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@PaymentActivity, "Berhasil diupload! Menunggu validasi Bapak Kos.", Toast.LENGTH_LONG).show()
                    finish() // Tutup halaman dan kembali ke Dashboard
                } else {
                    Toast.makeText(this@PaymentActivity, "Gagal mengunggah file", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TagihanResponse>, t: Throwable) {
                val btnSubmit = findViewById<Button>(R.id.btnSubmitPayment)
                btnSubmit.text = "KIRIM BUKTI PEMBAYARAN"
                btnSubmit.isEnabled = true
                Toast.makeText(this@PaymentActivity, "Koneksi Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi sakti untuk mengubah URI (Alamat Galeri) menjadi File nyata
    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile("struk_", ".jpg", context.cacheDir)

        val inputStream = contentResolver.openInputStream(selectedImg)
        val outputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream!!.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }
}
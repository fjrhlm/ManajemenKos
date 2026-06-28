package com.fjrhlm.manajemenkos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

    private var idTagihan: Int = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Ambil ID Tagihan yang dilempar dari MainActivity
        idTagihan = intent.getIntExtra("ID_TAGIHAN", 0)

        val btnSubmit = findViewById<Button>(R.id.btnSubmitPayment)
        val tvBack = findViewById<TextView>(R.id.tvBack)
        val tvHeaderTitle = findViewById<TextView>(R.id.tvHeaderTitle)
        val layInfoCard = findViewById<LinearLayout>(R.id.layInfoCard)
        val webView = findViewById<WebView>(R.id.webViewMidtrans)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Konfigurasi WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // Tangkap URL callback dari Midtrans
                if (url != null && (url.contains("transaction_status=settlement")
                            || url.contains("transaction_status=capture")
                            || url.contains("status_code=200"))) {
                    // Pembayaran berhasil
                    Toast.makeText(this@PaymentActivity, "Pembayaran Berhasil! ✅", Toast.LENGTH_LONG).show()
                    finish()
                    return true
                }
                if (url != null && (url.contains("transaction_status=pending"))) {
                    Toast.makeText(this@PaymentActivity, "Pembayaran Menunggu Konfirmasi ⏳", Toast.LENGTH_LONG).show()
                    finish()
                    return true
                }
                if (url != null && (url.contains("transaction_status=deny")
                            || url.contains("transaction_status=cancel")
                            || url.contains("transaction_status=expire"))) {
                    Toast.makeText(this@PaymentActivity, "Pembayaran Dibatalkan ❌", Toast.LENGTH_SHORT).show()
                    finish()
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Tombol Kembali
        tvBack.setOnClickListener {
            if (webView.visibility == View.VISIBLE) {
                // Jika WebView sedang terbuka, kembali ke info card
                webView.visibility = View.GONE
                progressBar.visibility = View.GONE
                layInfoCard.visibility = View.VISIBLE
                tvHeaderTitle.text = "Pembayaran"
            } else {
                finish()
            }
        }

        // Tombol Bayar — minta token dari server lalu load di WebView
        btnSubmit.setOnClickListener {
            btnSubmit.text = "Memproses..."
            btnSubmit.isEnabled = false

            ApiClient.instance.payTagihan(idTagihan).enqueue(object : Callback<MidtransResponse> {
                override fun onResponse(call: Call<MidtransResponse>, response: Response<MidtransResponse>) {
                    btnSubmit.text = "Bayar Sekarang"
                    btnSubmit.isEnabled = true

                    if (response.isSuccessful && response.body()?.status == "success") {
                        val redirectUrl = response.body()?.redirectUrl
                        if (redirectUrl != null) {
                            // Sembunyikan info card, tampilkan WebView
                            layInfoCard.visibility = View.GONE
                            webView.visibility = View.VISIBLE
                            progressBar.visibility = View.VISIBLE
                            tvHeaderTitle.text = "Pilih Metode Pembayaran"

                            // Load halaman Midtrans langsung di dalam WebView
                            webView.loadUrl(redirectUrl)
                        }
                    } else {
                        val errMsg = response.errorBody()?.string() ?: response.body()?.message ?: "Gagal memproses"
                        Toast.makeText(this@PaymentActivity, "Gagal: $errMsg", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<MidtransResponse>, t: Throwable) {
                    btnSubmit.text = "Bayar Sekarang"
                    btnSubmit.isEnabled = true
                    Toast.makeText(this@PaymentActivity, "Koneksi Error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    @Deprecated("Use onBackPressedDispatcher instead")
    override fun onBackPressed() {
        val webView = findViewById<WebView>(R.id.webViewMidtrans)
        if (webView.visibility == View.VISIBLE && webView.canGoBack()) {
            webView.goBack()
        } else if (webView.visibility == View.VISIBLE) {
            val layInfoCard = findViewById<LinearLayout>(R.id.layInfoCard)
            webView.visibility = View.GONE
            layInfoCard.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvHeaderTitle).text = "Pembayaran"
        } else {
            super.onBackPressed()
        }
    }
}
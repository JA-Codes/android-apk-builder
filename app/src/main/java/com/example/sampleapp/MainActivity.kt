package com.example.simplebrowser

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlBar: EditText

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        urlBar = findViewById(R.id.urlBar)

        // --- বেসিক প্রাইভেসি সেটিংস ---
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.saveFormData = false
        webView.settings.setGeolocationEnabled(false)

        // থার্ড-পার্টি কুকি বন্ধ রাখা (কিছুটা ট্র্যাকিং কমায়)
        android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false)

        // পেজের লিংকে ক্লিক করলে এই WebView-তেই খুলবে, বাইরের ব্রাউজারে না
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                urlBar.setText(url)
            }
        }

        webView.loadUrl("https://www.google.com")

        // ইউজার URL বারে লিখে Enter/Go চাপলে
        urlBar.setOnEditorActionListener { _, actionId, event ->
            val isEnterPressed = event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
            if (actionId == EditorInfo.IME_ACTION_GO || isEnterPressed) {
                loadFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun loadFromInput() {
        var input = urlBar.text.toString().trim()
        if (input.isEmpty()) return

        input = if (input.startsWith("http://") || input.startsWith("https://")) {
            input
        } else if (input.contains(".") && !input.contains(" ")) {
            "https://$input"
        } else {
            "https://www.google.com/search?q=" + input.replace(" ", "+")
        }
        webView.loadUrl(input)
    }

    // Back বাটন চাপলে আগের পেজে যাবে (একদম বেসিক)
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

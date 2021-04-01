package com.example.androidmyquote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.androidmyquote.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.lang.Exception
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getRandomQuotes()

        binding?.btnAllQuotes?.setOnClickListener {
            startActivity(Intent(this, ListQuotesActivity::class.java))
        }
    }

    private fun getRandomQuotes() {
        binding?.progressBar?.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/random"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                binding?.progressBar?.visibility = View.INVISIBLE

                val result = String(responseBody!!)

                Log.d("Debug", result)
                try {
                    val responseObject = JSONObject(result)

                    val quote = responseObject.getString("en")
                    val author = responseObject.getString("author")

                    binding?.tvQuote?.text = quote
                    binding?.tvAuthor?.text = author
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                binding?.progressBar?.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    0 -> "No Internet Access"
                    else -> "$statusCode : ${error?.message}"
                }
                Log.d("Debug", errorMessage)
//                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                Snackbar.make(binding?.root!!, errorMessage, Snackbar.LENGTH_INDEFINITE).apply {
                    setAction("Refresh") {
                        getRandomQuotes()
                    }
                    show()
                }

            }

        })
    }
}
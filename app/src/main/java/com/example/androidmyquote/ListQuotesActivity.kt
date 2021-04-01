package com.example.androidmyquote

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidmyquote.databinding.ActivityListQuotesBinding
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import java.lang.Exception

class ListQuotesActivity : AppCompatActivity() {

    private var binding: ActivityListQuotesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuotesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "List of Quotes"

        getListQuotes()
    }

    private fun getListQuotes() {
        binding?.progressBar?.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/list"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                binding?.progressBar?.visibility = View.GONE

                val listQuote = ArrayList<String>()

                val result = String(responseBody!!)
                Log.d("Debug", result)

                try {
                    val jsonArray = JSONArray(result)
                    Log.d("Debug", jsonArray.toString())

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val quote = jsonObject.getString("en")
                        val author = jsonObject.getString("author")
                        listQuote.add("\n$quote\n - $author\n")
                    }

                    val adapter = ArrayAdapter(this@ListQuotesActivity, R.layout.simple_list_item_1, listQuote)
                    binding?.listQuotes?.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this@ListQuotesActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding?.progressBar?.visibility = View.GONE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    0 -> "No Internet Access"
                    else -> "$statusCode : ${error?.message}"
                }
//                Snackbar.make(View(), , errorMessage, Toast.LENGTH_SHORT).show()
                val snack = Snackbar.make(
                    this@ListQuotesActivity,
                    binding?.root!!,
                    errorMessage,
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction("Refresh") {
                        getListQuotes()
                    }
                    show()
                }
            }
        })
    }
}
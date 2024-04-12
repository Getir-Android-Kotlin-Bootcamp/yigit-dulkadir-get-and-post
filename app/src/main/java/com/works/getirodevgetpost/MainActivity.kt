package com.works.getirodevgetpost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        getProfile("1")

        createPost("Kullanıcı Adı", "Başlık", "İçerik")
    }

    private fun getProfile(userId: String) {
        Thread {
            try {
                val profileUrl = URL("https://jsonplaceholder.typicode.com/users/$userId")
                (profileUrl.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inputStream.bufferedReader().use { reader ->
                            val profileResponse = reader.readText()
                            val profileJson = JSONObject(profileResponse)
                            val name = profileJson.getString("name")
                            val email = profileJson.getString("email")
                            val formattedText = "Name: $name\nEmail: $email"
                            runOnUiThread {
                                findViewById<TextView>(R.id.textViewGet).text = formattedText
                            }
                        }
                    } else {
                        runOnUiThread {
                            findViewById<TextView>(R.id.textViewGet).text = "Failed to retrieve user profile."
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun createPost(username: String, title: String, body: String) {
        Thread {
            try {
                val postUrl = URL("https://jsonplaceholder.typicode.com/posts")
                (postUrl.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    val postBody = JSONObject()
                    postBody.put("username", username)
                    postBody.put("title", title)
                    postBody.put("body", body)
                    OutputStreamWriter(outputStream).use { it.write(postBody.toString()) }
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        inputStream.bufferedReader().use { reader ->
                            val postResponse = reader.readText()
                            runOnUiThread {
                                findViewById<TextView>(R.id.textViewPost).text = postResponse
                            }
                        }
                    } else {
                        runOnUiThread {
                            findViewById<TextView>(R.id.textViewPost).text = "Failed to create post."
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}



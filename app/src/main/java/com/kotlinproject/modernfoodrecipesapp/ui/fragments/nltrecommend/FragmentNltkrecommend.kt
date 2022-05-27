package com.kotlinproject.modernfoodrecipesapp.ui.fragments.nltrecommend

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlinproject.modernfoodrecipesapp.R
import com.kotlinproject.ui.fragments.favorite.CustomAdapter
import kotlinx.android.synthetic.main.fragment_favorite_recipes.*
import kotlinx.android.synthetic.main.fragment_favorite_recipes.callButton
import kotlinx.android.synthetic.main.fragment_nltkrecommend.*
import kotlinx.android.synthetic.main.fragment_recipes.*
import okhttp3.*
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class FragmentNltkrecommend : Fragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        button()
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nltkrecommend, container, false)
    }
    private fun button(){

        var recyclerView = recyclerviewdeneme
        val data = arrayListOf<String>();

        callButton.setOnClickListener {
            val recipeSuggest =recipesuggest.text

            val client = OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build()

            val BASE_URL ="http://192.168.1.11:4000/hel?username="+recipeSuggest

            val request = Request.Builder()
                .url(BASE_URL)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val jsonString = response.body?.string()

                        try {
                            activity?.runOnUiThread(Runnable {
                                val json = JSONObject(jsonString)
                                //ekrana 1 resim bastırmak için 1 yaptık i yapınca sonuç alamıyorum.Amacım aslında 10 tane recipeı ve isimlerini göstermek
                                for (i in 0..10) {
                                    val deneme = removeQuotesAndUnescape(json.getString("data"))
                                    val gson = Gson()
                                    val cleanedDataNew : List<List<String>> = gson.fromJson(deneme, object : TypeToken<List<List<String>>>() {}.type)
                                    val adapter =NltkFragmentRecommendAdapter(context, cleanedDataNew)
                                    recyclerView.layoutManager = LinearLayoutManager(context)
                                    recyclerView.adapter = adapter
                                }
                                }) }

                        catch (e: IOException){
                            Log.e("Error", e.localizedMessage);
                        }
                    }

                }
            })
        }
    }
    private fun removeQuotesAndUnescape(uncleanJson: String): String? {
        val noQuotes = uncleanJson.replace("^\"|\"$".toRegex(), "")
        return StringEscapeUtils.unescapeJava(noQuotes)
    }
}



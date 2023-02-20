package com.example.mydialerwithfragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class ContactFragment : Fragment() {

    private val adapter: Adapter = Adapter { number ->
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(callIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://drive.google.com/u/0/uc?id=1-KO-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&export=download")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {

                    val recyclerView = view.findViewById<RecyclerView>(R.id.r_view)
                    val searchEditText = activity?.findViewById<EditText>(R.id.et_search)

                    val json = response.body()?.string()
                    val gson = GsonBuilder().create()
                    var phones = gson.fromJson(json, Array<Contact>::class.java)
                        .toList() as ArrayList<Contact>

                    activity?.runOnUiThread{
                        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                        recyclerView.adapter = adapter

                        adapter.submitList(phones)
                        searchEditText?.addTextChangedListener {
                            val filteredPhones = phones.filter { item ->
                                item.phone.lowercase().contains(it.toString().lowercase()) ||
                                        item.name.lowercase().contains(it.toString().lowercase()) ||
                                        item.type.lowercase().contains(it.toString().lowercase())
                            }
                            adapter.submitList(filteredPhones)

                            val filterState = activity?.getSharedPreferences("app_preferences",
                                AppCompatActivity.MODE_PRIVATE
                            )?.edit()
                            filterState?.putString("SEARCH_FILTER", it.toString())?.apply()
                        }

                        val lastFilterText = activity?.getSharedPreferences("app_preferences",
                            AppCompatActivity.MODE_PRIVATE
                        )?.getString("SEARCH_FILTER", "")
                        searchEditText?.append(lastFilterText)
                    }
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContactFragment()
    }
}
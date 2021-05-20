package com.mj.aop_part3_chapter04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.mj.aop_part3_chapter04.adapter.BookAdapter
import com.mj.aop_part3_chapter04.api.BookService
import com.mj.aop_part3_chapter04.databinding.ActivityMainBinding
import com.mj.aop_part3_chapter04.model.BestSellerDto
import com.mj.aop_part3_chapter04.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var bookService: BookService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBookRecyclerView()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKEY))
            .enqueue(object: Callback<BestSellerDto> {
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    if(response.isSuccessful.not()) {
                        return
                    }

                    response.body()?.let {
                        adapter.submitList(it.books)
                    }


                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })

        binding.searchEditText.setOnKeyListener { _, keyCode, keyEvent ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }


    }

    private fun search(keyword: String) {
        bookService.getBooksByName(getString(R.string.interparkAPIKEY), keyword)
            .enqueue(object: Callback<SearchBookDto> {
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    if(response.isSuccessful.not()) {
                        return
                    }
                    adapter.submitList(response.body()?.books.orEmpty())
                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun initBookRecyclerView(){
        adapter = BookAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    companion object {
        private const val TAG = "mainActivity"
    }

}
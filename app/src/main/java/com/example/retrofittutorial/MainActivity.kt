package com.example.retrofittutorial

import android.annotation.SuppressLint
import android.net.http.HttpException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofittutorial.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.IOException

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                binding.progressBar.isVisible = true
                val response = try {
                    RetrofitInstance.api.getTodos()
                } catch (e: IOException) {
                    Log.e(TAG, "IOException | You migth not have internet connection")
                    binding.progressBar.isVisible = false
                    return@repeatOnLifecycle
                } catch (@SuppressLint("NewApi", "LocalSuppress") e: HttpException) {
                    Log.e(TAG, "HttpException | You migth have unexpected response")
                    binding.progressBar.isVisible = false
                    return@repeatOnLifecycle
                }
                if(response.isSuccessful && response.body() != null){
                    todoAdapter.todos = response.body()!!
                }
                else{
                    Log.e(TAG, "Response not successful")
                }
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun setupRecyclerView() = binding.rvTodos.apply{
        todoAdapter = TodoAdapter()
        adapter = todoAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
    }
}
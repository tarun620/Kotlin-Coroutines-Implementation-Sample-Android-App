package com.tarun.example.coroutines.learn.task.onetask

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tarun.example.coroutines.R
import com.tarun.example.coroutines.data.api.ApiHelperImpl
import com.tarun.example.coroutines.data.api.RetrofitBuilder
import com.tarun.example.coroutines.data.local.DatabaseBuilder
import com.tarun.example.coroutines.data.local.DatabaseHelperImpl
import com.tarun.example.coroutines.data.model.ApiUser
import com.tarun.example.coroutines.learn.base.ApiUserAdapter
import com.tarun.example.coroutines.utils.Status
import com.tarun.example.coroutines.utils.ViewModelFactory
import kotlinx.android.synthetic.main.activity_long_running_task.*
import kotlinx.android.synthetic.main.activity_recycler_view.*
import kotlinx.android.synthetic.main.activity_recycler_view.progressBar

class LongRunningTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: LongRunningTaskViewModel
    private lateinit var adapter: ApiUserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_long_running_task)
        setContentView(R.layout.activity_recycler_view)
//        setupViewModel()
//        setupLongRunningTask()

        setupUI()
        setupViewModel()
        setupObserver()
    }

    private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =
            ApiUserAdapter(
                arrayListOf()
            )
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

//    private fun setupLongRunningTask() {
//        viewModel.getStatus().observe(this, Observer {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    progressBar.visibility = View.GONE
//                    textView.text = it.data
//                    textView.visibility = View.VISIBLE
//                }
//                Status.LOADING -> {
//                    progressBar.visibility = View.VISIBLE
//                    textView.visibility = View.GONE
//                }
//                Status.ERROR -> {
//                    //Handle Error
//                    progressBar.visibility = View.GONE
//                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                }
//            }
//        })
//        viewModel.startLongRunningTask()
//    }

    private fun setupObserver() {
        viewModel.getUsers().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { users -> renderList(users) }
                    recyclerView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun renderList(users: List<ApiUser>) {
        adapter.addData(users)
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(LongRunningTaskViewModel::class.java)
    }
}

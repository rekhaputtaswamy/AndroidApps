package fr.epita.retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epita.retrofit.model.People
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var dataList = ArrayList<People>()

    lateinit var imageView: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var prgressDialog: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.logo)
        recyclerView = findViewById(R.id.recycler_view)
        prgressDialog = findViewById(R.id.progressBar)

        //Glide.with(this).load("").into(imageView)

        //setting up the adapter
        recyclerView.adapter = DataAdapter(dataList,this)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        prgressDialog.visibility = View.VISIBLE
        getData()
    }

    private fun getData() {
        val call: Call<List<People>> = ApiClient.getClient.getPeople()
        call.enqueue(object: Callback<List<People>> {
            override fun onResponse(call: Call<List<People>>?, response: Response<List<People>>?) {
                prgressDialog.visibility = View.GONE
                dataList.addAll(response!!.body()!!)
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<People>>?, t: Throwable?) {
                prgressDialog.visibility = View.GONE
            }
        })
    }
}

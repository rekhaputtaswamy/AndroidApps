package fr.epita.retrofit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.epita.retrofit.model.People

class DataAdapter(private var dataList: List<People>, private val context: Context) :
                                                RecyclerView.Adapter<DataAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_home, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTextView.text = dataList[position].name
        holder.ratingTextView.text = dataList[position].age

    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
        lateinit var androidLayout: View
        lateinit var titleTextView: TextView
        lateinit var iconImageView: ImageView
        lateinit var ratingTextView: TextView

        init {
            androidLayout = itemView.findViewById(R.id.card_view)
            titleTextView = itemLayoutView.findViewById(R.id.title)
            iconImageView = itemLayoutView.findViewById(R.id.gender_img)
            ratingTextView = itemLayoutView.findViewById(R.id.rating)
        }
    }
}
package com.hellowo.teamfinder.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.model.History
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_item_history.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("IMPLICIT_CAST_TO_ANY")
class HistoryAdapter(val context: Context, val itemList: OrderedRealmCollection<History>,
                     val adapterInterface: (item: History?) -> Unit)
    : RealmRecyclerViewAdapter<History, HistoryAdapter.ViewHolder>(itemList, true),
        StickyRecyclerHeadersAdapter<HistoryAdapter.HeaderHolder> {

    val cal: Calendar = Calendar.getInstance()
    
    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)
    inner class HeaderHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): HistoryAdapter.HeaderHolder
            = HeaderHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_group_char, parent, false))

    override fun getHeaderId(position: Int): Long {
        cal.timeInMillis = getItem(position)?.dtStart as Long
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    override fun onBindHeaderViewHolder(holder: HistoryAdapter.HeaderHolder?, position: Int) {
        holder?.itemView?.findViewById<TextView>(R.id.titleText)?.text =
                DateFormat.getDateInstance(DateFormat.LONG).format(Date(getItem(position)?.dtStart as Long))
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_history, parent, false))

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        val v = holder.itemView
        getItem(position)?.let{ item ->
            v.callStatusImage.setImageResource(
                    when(item.type) {
                        0 -> R.drawable.ic_call_received_black_24dp
                        1 -> R.drawable.ic_call_made_black_24dp
                        2 -> R.drawable.ic_call_missed_black_24dp
                        else -> R.drawable.ic_call_made_black_24dp
                    }
            )

            v.callStatusImage.setColorFilter(
                    when(item.type) {
                        0 -> context.resources.getColor(R.color.colorPrimary)
                        1 -> context.resources.getColor(R.color.colorAccent)
                        2 -> context.resources.getColor(R.color.red)
                        else -> context.resources.getColor(R.color.colorPrimary)
                    }
            )

            v.timeText.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(item.dtStart as Long))

            v.setOnClickListener { adapterInterface.invoke(item) }
        }
    }
}

package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.model.History
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class HistoryAdapter(val context: Context, val itemList: OrderedRealmCollection<History>,
                     val adapterInterface: (item: History?) -> Unit)
    : RealmRecyclerViewAdapter<History, HistoryAdapter.ViewHolder>(itemList, true) {

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_basic, parent, false))

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        val v = holder.itemView
        v.setOnClickListener { adapterInterface.invoke(item) }
    }
}
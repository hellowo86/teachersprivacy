package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.model.Student
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_student.view.*

class StudentAdapter(val context: Context, itemList: OrderedRealmCollection<Student>,
                     val adapterInterface: (item: Student?) -> Unit)
    : RealmRecyclerViewAdapter<Student, StudentAdapter.ViewHolder>(itemList, true),
        StickyRecyclerHeadersAdapter<StudentAdapter.HeaderHolder> {

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)
    inner class HeaderHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): HeaderHolder
            = HeaderHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_group_char, parent, false))

    override fun getHeaderId(position: Int): Long = getItem(position)?.name?.get(0)?.toLong() as Long

    override fun onBindHeaderViewHolder(holder: HeaderHolder?, position: Int) {
        holder?.itemView?.findViewById<TextView>(R.id.titleText)?.text = getItem(position)?.name?.get(0).toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_basic, parent, false))

    override fun onBindViewHolder(holder: StudentAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        val v = holder.itemView
        v.titleText.text = item?.name
        v.setOnClickListener { adapterInterface.invoke(item) }
    }
}
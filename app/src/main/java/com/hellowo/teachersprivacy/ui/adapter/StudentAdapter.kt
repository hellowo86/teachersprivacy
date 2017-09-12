package com.hellowo.teamfinder.ui.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.model.Student
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.list_item_student.view.*
import java.io.File
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri


class StudentAdapter(val activity: Activity, itemList: OrderedRealmCollection<Student>,
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
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_student, parent, false))

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: StudentAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        val v = holder.itemView
        item?.let {
            v.titleText.text = item.name

            if(item.memo.isNullOrEmpty()) {
                v.subText.visibility = View.GONE
            }else {
                v.subText.visibility = View.VISIBLE
                v.subText.text = it.memo
            }

            if(item.profileImageUrl.isNullOrEmpty()) {
                v.imageView.setImageResource(R.drawable.boy)
            }else {
                Glide.with(activity).load(File(item.profileImageUrl)).bitmapTransform(CropCircleTransformation(activity)).into(v.imageView)
            }

            v.lastCallText.visibility = View.GONE

            v.callBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.phoneNumber))
                activity.startActivity(intent)
            }
            v.setOnClickListener { adapterInterface.invoke(item) }
        }
    }
}
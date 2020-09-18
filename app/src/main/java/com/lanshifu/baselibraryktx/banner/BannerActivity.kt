package com.lanshifu.baselibraryktx.banner

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.base.BaseActivity
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils
import kotlinx.android.synthetic.main.activity_bannert.*


/**
 * @author lanxiaobin
 * @date 2020/9/3.
 */
class BannerActivity : BaseActivity() {

    val list = mutableListOf<String>()

    override fun getLayoutResId(): Int {
        return R.layout.activity_bannert
    }

    override fun initView() {

        list.add("0")
        list.add("1")
        list.add("2")
        list.add("3")

        val adapter = MyAdapter()
        val adapter3 = MyAdapter()
//
        blVertical.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
        recyclerView.adapter = adapter3
        PagerSnapHelper().attachToRecyclerView(recyclerView)

    }

    override fun initData() {

    }

    inner class MyAdapter : BannerAdapter<String, ImageHolder>(list) {

        override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
            val itemView = BannerUtils.getView(parent, R.layout.item_test)
            return ImageHolder(itemView)
        }

        override fun onBindView(holder: ImageHolder, data: String, position: Int, size: Int) {
            holder.titleTextView.setText(data)
        }

    }

    class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = itemView.findViewById(R.id.mImage) as ImageView
        var titleTextView: TextView = itemView.findViewById(R.id.tvTitle) as TextView
    }
}
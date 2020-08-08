package com.lanshifu.baselibraryktx.list

import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.base.BaseActivity
import com.lanshifu.lib.ext.toast
import kotlinx.android.synthetic.main.activity_list.*


/**
 * @author lanxiaobin
 * @date 2020/8/9
 */
class DemoListActivity : BaseActivity() {

    var list = ArrayList<String>()
    var mAdapter: MyAdapter = MyAdapter()

    override fun getLayoutResId(): Int {
        return R.layout.activity_list
    }

    override fun initView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
    }

    override fun initData() {

        list.add("1")
        list.add("2")
        list.add("3")
        list.add("4")


        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            toast("OnItemClick：$position")
        }

        mAdapter.setOnItemChildClickListener { adapter, view, position ->

            when (view.id) {
                R.id.mImage ->
                    toast("点击了图片：$position")
            }
        }
    }

    inner class MyAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_test, list) {
        override fun convert(helper: BaseViewHolder, item: String?) {
            helper.setText(R.id.tvTitle, item)
                .addOnClickListener(R.id.mImage)

        }

    }
}
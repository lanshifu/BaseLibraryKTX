package com.lanshifu.baselibraryktx.viewpager

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.base.BaseActivity
import com.lanshifu.lib.core.lifecycle.LifecycleHandler
import com.lanshifu.lib.ext.loge
import com.lanshifu.lib.ext.logi
import com.lanshifu.lib.ext.logw
import com.lanshifu.lib.ext.toast
import kotlinx.android.synthetic.main.activity_viewpager.*


/**
 * @author lanxiaobin
 * @date 3/11/21
 */
class ViewPagerActivity : BaseActivity() {

    private var mRoomDragging = false
    private val mHandler by lazy { LifecycleHandler(this) }
    private var mCurrentPosition = -1

    override fun getLayoutResId(): Int {
        return R.layout.activity_viewpager
    }

    override fun initView() {
        var linearLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        mRecyclerView.layoutManager = linearLayoutManager

        linearLayoutManager.initialPrefetchItemCount
        //每次只能滑动一页
        val kSongRoomSnapHelper = PagerSnapHelper()
        kSongRoomSnapHelper.attachToRecyclerView(mRecyclerView)

        val list = mutableListOf<String>()
        list.add("1")
        list.add("2")
        list.add("3")
        mRecyclerView.adapter = MyAdapter(list)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mRoomDragging = true
                    return
                }
                mRoomDragging = false

                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return
                }

                try {
                    val layoutParams =
                        (recyclerView.getChildAt(0).layoutParams) as RecyclerView.LayoutParams
                    val position = layoutParams.viewAdapterPosition
                    if (mCurrentPosition == position) {
                        logw("mCurrentPosition==$mCurrentPosition,ignore")
                        return
                    }
                    mCurrentPosition = layoutParams.viewAdapterPosition
                    logi("OnScrollListener,SCROLL_STATE_IDLE,position=$mCurrentPosition")

                    mHandler.removeCallbacks(joinRoomRunnable)
                    mHandler.postDelayed(joinRoomRunnable, 1000)
                } catch (e: Exception) {
                    loge("${e.message}")
                }

            }
        })

        if (mCurrentPosition != 0) {
            mCurrentPosition = 0
            joinRoomRunnable.run()
        }
    }


    private val joinRoomRunnable = Runnable {

        logi("joinRoomRunnable")
        toast("进入新房间,mCurrentPosition=$mCurrentPosition")
    }

    override fun initData() {

    }


    inner class MyAdapter(list: MutableList<String>) :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_viewpager, list) {
        override fun convert(helper: BaseViewHolder, item: String?) {
            helper.setText(R.id.tvTitle, item)
                .addOnClickListener(R.id.mImage)

        }

    }
}
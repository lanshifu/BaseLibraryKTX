package com.lanshifu.baselibraryktx.animations

import android.os.Build
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.base.BaseVMFragment
import com.lanshifu.lib.ext.logi
import kotlinx.android.synthetic.main.activity_sharedelement_fragment1.*

/**
 * Created by lgvalle on 05/09/15.
 */
class SharedElementFragment1 : BaseVMFragment<SharedElementVM>() {


    override fun getLayoutResId(): Int {
        return R.layout.activity_sharedelement_fragment1
    }

    override fun initView() {
        sample2_button1.setOnClickListener { addNextFragment(square_blue, false) }
        sample2_button2.setOnClickListener { addNextFragment(square_blue, true) }

        btnPre.setOnClickListener {
            mViewModel.pre()
        }
        btnNext.setOnClickListener {
            mViewModel.next()
        }

        mViewModel.titleUpdateResult.observe(this, Observer {
            tvTitle.text = it
        })

        mViewModel.progressUpdateResult.observe(this, Observer {
            tvDuration.text = "progress:$it"
        })

    }

    override fun initData() {


        mViewModel.startPlay()
    }

    private fun addNextFragment(
        squareBlue: ImageView,
        overlap: Boolean
    ) {
        val sharedElementFragment2 = SharedElementFragment2.newInstance(mViewModel)

        var slideTransition: Slide? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slideTransition = Slide(Gravity.RIGHT)
            slideTransition.duration = 300L
        }

        val changeBoundsTransition = ChangeBounds()
        changeBoundsTransition.duration = 300L

        sharedElementFragment2.allowEnterTransitionOverlap = overlap
        sharedElementFragment2.allowReturnTransitionOverlap = overlap
        //设置进入动画和共享元素进入动画
        sharedElementFragment2.enterTransition = slideTransition
        sharedElementFragment2.sharedElementEnterTransition = changeBoundsTransition
        fragmentManager!!.beginTransaction()
            .replace(R.id.sample2_content, sharedElementFragment2)
            .addToBackStack(null)
            .addSharedElement(
                squareBlue,
                getString(R.string.square_blue_name)
            )
            .addSharedElement(
                view!!.findViewById(R.id.btnPre),
                "btnPre"
            )
            .addSharedElement(
                view!!.findViewById(R.id.btnPlay),
                "btnPlay"
            )
            .addSharedElement(
                view!!.findViewById(R.id.btnNext),
                "btnNext"
            )
            .addSharedElement(
                view!!.findViewById(R.id.tvTitle),
                "tvTitle"
            )
            .addSharedElement(
                view!!.findViewById(R.id.tvDuration),
                "tvDuration"
            )
            .commit()
    }

    companion object {
        private const val EXTRA_SAMPLE = "sample"

        @JvmStatic
        fun newInstance(sample: Sample?): SharedElementFragment1 {
            val args = Bundle()
            args.putSerializable(EXTRA_SAMPLE, sample)
            val fragment = SharedElementFragment1()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        logi("onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logi("onDestroyView")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logi("onViewCreated")
    }

    override fun onResume() {
        super.onResume()
        logi("onResume")
    }

    override fun onPause() {

        super.onPause()
        logi("onPause")
    }

    override fun onStop() {
        super.onStop()
        logi("onStop")
    }
}
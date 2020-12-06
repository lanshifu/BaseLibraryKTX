package com.lanshifu.baselibraryktx.animations

import android.os.Bundle
import androidx.lifecycle.Observer
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.base.BaseFragment
import kotlinx.android.synthetic.main.activity_sharedelement_fragment1.*

class SharedElementFragment2 : BaseFragment() {

    var mViewModel: SharedElementVM? = null

    override fun getLayoutResId(): Int {
        return R.layout.activity_sharedelement_fragment2
    }

    override fun initView() {

        btnPre.setOnClickListener {
            mViewModel?.pre()
        }
        btnNext.setOnClickListener {
            mViewModel?.next()
        }
    }

    override fun initData() {

        mViewModel?.titleUpdateResult?.observe(this, Observer {
            tvTitle.text = it
        })

        mViewModel?.progressUpdateResult?.observe(this, Observer {
            tvDuration.text = "progress:$it"
        })

    }

    companion object {

        fun newInstance(viewModel: SharedElementVM): SharedElementFragment2 {

            val args = Bundle()
//            args.putSerializable(EXTRA_SAMPLE, sample)
            val fragment = SharedElementFragment2()
            fragment.mViewModel = viewModel
            fragment.arguments = args
            return fragment
        }

    }
}
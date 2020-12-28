package com.lanshifu.baselibraryktx.slidebar

import androidx.recyclerview.widget.LinearLayoutManager
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_contact.*


/**
 * @author lanxiaobin
 * @date 2020/12/28
 */
class SlideBarActivity :BaseActivity() {
    private val contacts: ArrayList<Contact> = ArrayList()

    override fun getLayoutResId(): Int {
        return R.layout.activity_contact
    }

    override fun initView() {


        rv_contacts.layoutManager = LinearLayoutManager(this)
        rv_contacts.adapter = ContactsAdapter(contacts,R.layout.item_contacts)

        side_bar.setOnSelectIndexItemListener {index ->
            for (i in contacts.indices) {
                if (contacts[i].index == index) {
                    (rv_contacts.getLayoutManager() as LinearLayoutManager).scrollToPositionWithOffset(
                        i,
                        0
                    )
                    return@setOnSelectIndexItemListener
                }
            }
        }

    }

    override fun initData() {
        contacts.addAll(Contact.getEnglishContacts());
    }
}
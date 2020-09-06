package com.lanshifu.baselibraryktx.record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import com.lanshifu.baselibraryktx.R
import com.lanshifu.mp3recorddemo.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_record.*
import java.io.File

//https://github.com/CarGuo/GSYRecordWave
class RecordActivity : AppCompatActivity() {

    private var mMP3Recorder: MP3Recorder? = null   //音频工具
    private var mFilePath: String = ""              //文件路径

    private val mAudioPlayer by lazy { AudioPlayer(this, Handler()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        //一些初始化工作
        initView()
        //view监听...
        initListener()
    }

    private fun initListener() {
        //开始录音
        bthAudioStart.setOnClickListener {
            //录音权限
            PermissionUtils.audio(this) {
                mMP3Recorder?.start()
            }
        }

        //停止录音
        bthAudioStop.setOnClickListener {
            mMP3Recorder?.let {
                it.stop()
                //设置文件路径
                tvAudioPath.text = "路径：$mFilePath"
            }
        }

        bthAudioPlay.setOnClickListener {
            mFilePath?.let {
                Log.i("lxb","playUrl,mFilePath=$mFilePath")
                mAudioPlayer.playUrl(mFilePath)
            }
        }

    }

    private fun initView() {
        //读写权限判断
        PermissionUtils.readAndWrite(this) {
            //获取保存的文件
            val file = File(Environment.getExternalStorageDirectory(),"test.mp3")
            mFilePath = file.absolutePath
            mMP3Recorder = MP3Recorder(file)
            Log.i("lxb","mFilePath=$mFilePath")
        }

    }
}

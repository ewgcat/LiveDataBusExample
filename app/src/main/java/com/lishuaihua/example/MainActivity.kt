package com.lishuaihua.example

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.lishuaihua.example.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        LiveDataBus.get().with("string", String::class.java)!!.observe(this, Observer {
            binding.tvMainMessahe.setText(it)
        })

        LiveDataBus.get().with("newsBean", NewsBean::class.java)!!.observe(this, Observer {
            binding.tvThreadMessahe.setText(it.desc)
        })
    }


    /**
     * 主线程发送消息
     */
    fun sendMessage(view: View) {
        LiveDataBus.get().with("string", String::class.java)!!.postValue("主线程发送的消息")
    }


    /**
     * 子线程发送消息
     */
    fun threadSendMessage(view: View) {
        Thread(Runnable {
            val newsBean = NewsBean("newsBean", "子线程发送的消息")
            LiveDataBus.get().with("newsBean", NewsBean::class.java)!!.postValue(newsBean)
        }).start()

    }

}

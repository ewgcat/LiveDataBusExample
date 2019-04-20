package com.lishuaihua.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lishuaihua.example.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        LiveDataBus.get().with("string",String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(MainActivity.this,s+"11111",Toast.LENGTH_SHORT).show();
            }
        });

        LiveDataBus.get().with("newsBean", NewsBean.class).observe(this, new Observer<NewsBean>() {
            @Override
            public void onChanged(@Nullable NewsBean newsBean) {
                Toast.makeText(MainActivity.this, newsBean.getName()+ newsBean.getType()+"11111",Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 主线程发送消息
     */
    public void sendMessage(View view) {
        NewsBean newsBean =new NewsBean("newsBean","主线程发送的消息");
        LiveDataBus.get().with("newsBean", NewsBean.class).postValue(newsBean);
    }



    /**
     * 子线程发送消息
     */
    public void threadSendMessage(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NewsBean newsBean =new NewsBean("newsBean","子线程发送的消息");
                LiveDataBus.get().with("newsBean", NewsBean.class).postValue(newsBean);
            }
        }).start();

    }

}

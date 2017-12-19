package com.example.yizhan.progresssets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //第一种跳过
//        ThreeSecondJump0 progress1 = (ThreeSecondJump0) findViewById(R.id.progress_1);
//        progress1.start(5, new ThreeSecondJump0.JumpFinishCallback() {
//            @Override
//            public void onFinish() {
//                Toast.makeText(MainActivity.this, "5秒倒计时完成", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onClick() {
//                Toast.makeText(MainActivity.this, "直接跳过", Toast.LENGTH_SHORT).show();
//            }
//        });

//        ThreeSecondJump1 progress2 = (ThreeSecondJump1) findViewById(R.id.progress_2);
//        progress2.start(8000, new ThreeSecondJump1.JumpFinishCallback() {
//            @Override
//            public void onFinish() {
//                Toast.makeText(MainActivity.this, "8秒倒计时完成", Toast.LENGTH_SHORT).show();
//            }
//        });

//        Progress1 progress1 = (Progress1) findViewById(R.id.progress_3);
//        progress1.show();

        Progress2 progress2 = (Progress2) findViewById(R.id.progress_4);
        progress2.show();

    }
}

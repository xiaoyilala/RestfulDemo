package com.ice.good.lib.lib.restful.demo.xy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ice.good.lib.lib.restful.demo.R
import kotlinx.android.synthetic.main.activity_xy.*

class XYTextActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xy)

        v1.post {
            Log.e("XYTextActivity", "offset" + v1.x)
            Log.e("XYTextActivity", "offset" + v1.left)
        }


        v2.post {
            Log.e("XYTextActivity", "translation" + v2.x)
            Log.e("XYTextActivity", "translation" + v2.left)
        }

        v1.setOnClickListener {
            Log.d("scroll", "v1")
        }

        v2.setOnClickListener {
            Log.d("translation", "v22")
        }


        btn1.setOnClickListener {
            //offset会改变getleft的值
            v1.offsetLeftAndRight(100)
            v1.offsetTopAndBottom(100)

            btn1.post {
                Log.e("XYTextActivity", "offset" + v1.translationX)
                Log.e("XYTextActivity", "offset" + v1.x)
                Log.e("XYTextActivity", "offset" + v1.left)
            }

        }

        btn2.setOnClickListener {
            val tx = v2.translationX
            if(tx==0f){
                v2.translationX=100f
                v2.translationY=100f
            }else{
                v2.translationX=0f
                v2.translationY=0f
            }

            //translationX 不会改变getleft的值
            btn2.post {
                Log.e("XYTextActivity", "translation" + v2.translationX)
                Log.e("XYTextActivity", "translation" + v2.x)
                Log.e("XYTextActivity", "translation" + v2.left)
            }
        }
    }
}
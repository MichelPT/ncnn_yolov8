// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.
package com.example.testingyolov8ncnn

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity(), SurfaceHolder.Callback {
    private val yolov8ncnn = Yolov8Ncnn()
    private var facing = 1

    private var spinnerModel: Spinner? = null
    private var spinnerCPUGPU: Spinner? = null
    private var current_model = 0
    private var current_cpugpu = 0

    private var cameraView: SurfaceView? = null

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        cameraView = findViewById<View?>(R.id.cameraview) as SurfaceView

        cameraView?.getHolder()?.setFormat(PixelFormat.RGBA_8888)
        cameraView?.getHolder()?.addCallback(this)

//        val buttonSwitchCamera = findViewById<View?>(R.id.buttonSwitchCamera) as Button
//        buttonSwitchCamera.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(arg0: View?) {
//                val new_facing = 1 - facing
//
//                yolov8ncnn.closeCamera()
//
//                yolov8ncnn.openCamera(new_facing)
//
//                facing = new_facing
//            }
//        })
//
//        spinnerModel = findViewById<View?>(R.id.spinnerModel) as Spinner
//        spinnerModel?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                arg0: AdapterView<*>?,
//                arg1: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (position != current_model) {
//                    current_model = position
//                    reload()
//                }
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>?) {
//            }
//        })

//        spinnerCPUGPU = findViewById<View?>(R.id.spinnerCPUGPU) as Spinner
//        spinnerCPUGPU?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                arg0: AdapterView<*>?,
//                arg1: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (position != current_cpugpu) {
//                    current_cpugpu = position
//                    reload()
//                }
//            }

//            override fun onNothingSelected(arg0: AdapterView<*>?) {
//            }
//        })
//
        reload()
    }

    private fun reload() {
        val ret_init: Boolean = yolov8ncnn.loadModel(getAssets(), current_model, current_cpugpu)
        if (!ret_init) {
            Log.e("MainActivity", "yolov8ncnn loadModel failed")
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        yolov8ncnn.setOutputWindow(holder.getSurface())
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    public override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.CAMERA
            ) === PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.CAMERA),
                REQUEST_CAMERA
            )
        }

        yolov8ncnn.openCamera(facing)
    }

    public override fun onPause() {
        super.onPause()

        yolov8ncnn.closeCamera()
    }

    companion object {
        const val REQUEST_CAMERA: Int = 100
    }
}

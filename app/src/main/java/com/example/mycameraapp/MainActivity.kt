package com.example.mycameraapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.TextureView
import android.view.Surface
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.mycameraapp.ui.theme.MyCameraAppTheme
import android.widget.SeekBar
import android.widget.Toast

class MainActivity : ComponentActivity() {
    lateinit var capReq: CaptureRequest.Builder
    lateinit var handler: Handler
    lateinit var handlerThread: HandlerThread
    lateinit var cameraManager: CameraManager
    lateinit var textureView: TextureView
    lateinit var cameraCaptureSession: CameraCaptureSession
    lateinit var cameraDevice: CameraDevice
    lateinit var captureRequest: CaptureRequest
    lateinit var btnShowDlg: Button
    lateinit var seek: SeekBar
    var cid: Int = 0
    var ll: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activiti_main)
        seek = findViewById<SeekBar>(R.id.scam)
        var id: String = "0"
        seek?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                cid = progress
                try {
                    ll = cameraManager.cameraIdList.lastIndex
                    if (seek.max !=ll.toInt()){
                        seek.max = ll
                    }
                    cameraCaptureSession.stopRepeating()
                    cameraDevice.close()
                    id = cameraManager.cameraIdList[cid]
                } catch (e: Exception) {
                    // handler
                }
                open_camera()
            // write custom code for progress is changed
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                Toast.makeText(this@MainActivity,
                    "Progress is: " + seek.progress + "%"+" ll "+ll+" id "+id,
                    Toast.LENGTH_SHORT).show()
            }
        })
//        setContent {
//            MyCameraAppTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//                    Greeting("Android")
//                }
//            }
//        }
        get_permissions()
        textureView = findViewById(R.id.textureView)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler((handlerThread).looper)
        textureView.surfaceTextureListener =  object: TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                open_camera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
//                TODO("Not yet implemented")
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
//                TODO("Not yet implemented")
            }
        }

    }

    @SuppressLint("MissingPermission")
   fun open_camera(){
       if (ActivityCompat.checkSelfPermission(
               this,
               Manifest.permission.CAMERA
           ) != PackageManager.PERMISSION_GRANTED
       ) {
           // TODO: Consider calling
           //    ActivityCompat#requestPermissions
           // here to request the missing permissions, and then overriding
           //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
           //                                          int[] grantResults)
           // to handle the case where the user grants the permission. See the documentation
           // for ActivityCompat#requestPermissions for more details.
           return
       }
       //seek.max = cameraManager.cameraIdList.lastIndex
       cameraManager.openCamera(cameraManager.cameraIdList[cid], object: CameraDevice.StateCallback(){

           override fun onOpened(p0: CameraDevice) {
               cameraDevice = p0
               var capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
//               var surface = Surface(textureView.surfaceTexture)
               var surface = Surface(textureView.surfaceTexture)
               capReq.addTarget(surface)
               cameraDevice.createCaptureSession(listOf(surface), object: CameraCaptureSession.StateCallback(){
                   override fun onConfigured(p0: CameraCaptureSession) {
                       cameraCaptureSession = p0
                       cameraCaptureSession.setRepeatingRequest(capReq.build(), null, null)
                   }

                   override fun onConfigureFailed(p0: CameraCaptureSession) {
//                      // TODO("Not yet implemented")
                   }
               },handler)
           }

           override fun onDisconnected(p0: CameraDevice) {
//               TODO("Not yet implemented")
           }

           override fun onError(p0: CameraDevice, p1: Int) {
//               TODO("Not yet implemented")
           }
       },handler)
   }

    fun get_permissions(){
        var permissionsLst = mutableListOf<String>()
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) permissionsLst.add(android.Manifest.permission.CAMERA)
        if (permissionsLst.size >0){
            requestPermissions(permissionsLst.toTypedArray(),  101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         grantResults.forEach{
             if(it !=PackageManager.PERMISSION_GRANTED){
                 get_permissions()
             }
         }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyCameraAppTheme {
        Greeting("Android")
    }
}
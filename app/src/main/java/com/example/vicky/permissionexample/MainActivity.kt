package com.example.vicky.permissionexample

import android.Manifest
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import java.io.*

private const val FILE_PICKER_ID = 12
private const val PERMISSION_REQUEST = 10

class MainActivity : AppCompatActivity() {

    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this


        btn_request.setOnClickListener({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission(context, permissions)) {
                    Toast.makeText(context, "Permission are already provided", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, PERMISSION_REQUEST)
                }
            }else{
                Toast.makeText(context, "Permission are already provided", Toast.LENGTH_SHORT).show()
            }
        })

        btn_camera.setOnClickListener({
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            startActivity(intent)
        })

        btn_file.setOnClickListener({
            intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, FILE_PICKER_ID)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER_ID && resultCode == Activity.RESULT_OK) {
            val dest = File(PathUtils.getPath(context, data.data))
            val inputAsString = FileInputStream(dest).bufferedReader().use { it.readText() }
            btn_file.text = inputAsString
        }
    }

    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices){
            if(checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST){
            var allSuccess = true
            for(i in permissions.indices){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false
                    var requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if(requestAgain){
                        Toast.makeText(context,"Permission denied",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"Go to settings and enable the permission",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if(allSuccess)
                Toast.makeText(context,"Permissions Granted",Toast.LENGTH_SHORT).show()

        }
    }

}

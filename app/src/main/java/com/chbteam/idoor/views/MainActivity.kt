package com.chbteam.idoor.views

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper.prepare
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.chbteam.idoor.R
import com.chbteam.idoor.network.ApiInterceptor
import com.chbteam.idoor.utilities.showProgress
import io.reactivex.schedulers.Schedulers.start
import java.io.File
import java.lang.Exception


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(){

    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    private var listOfProfiles = arrayOf<Profile>()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_main)

        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.wav"

        ActivityCompat.requestPermissions(this, permissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )

        record.setOnClickListener {
            val mStartRecording = true
            onRecord(mStartRecording)
            micImg.setImageResource(R.drawable.mic)
            Handler().postDelayed({
                onRecord(false)
                micImg.setImageResource(R.drawable.mic_png)
            }, 5000)
        }

        play.setOnClickListener {
            onPlay(true)
        }

        addProfile.setOnClickListener {
            /*val progress = showProgress(this.applicationContext)
            progress.show()*/
            ApiInterceptor(context = this.applicationContext).getNewProfile().observe(this, Observer {profile ->
                if(profile!=null){
                    //progress.dismiss()
                    val mStartRecording = true
                    onRecord(mStartRecording)
                    micImg.setImageResource(R.drawable.mic)
                    Handler().postDelayed({
                        onRecord(false)
                        micImg.setImageResource(R.drawable.mic_png)
                        enrollProfile(profile)
                    }, 10000)
                }
            })
        }

    }

    fun enrollProfile(profileId : String){
        try {
            val file = File(fileName)

            val uri = FileProvider.getUriForFile(
                this,
                this.packageName + ".provider",
                file
            )

            ApiInterceptor(context = this.applicationContext).enrollProfile(uri,profileId,this).observe(this, Observer { result ->
                if(result!=null){
                    Log.d("RESULT",result)
                }
            })
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }
}


class Profile(name : String, id : String){
    var ProfileName : String = ""
    var ProfileId : String = ""

    init {
        this.ProfileId = id
        this.ProfileName = name
    }
}
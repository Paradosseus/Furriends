package com.innovaid.furriends.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.react.modules.core.PermissionListener
import com.google.firebase.auth.FirebaseAuth
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.User
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_video_call.*
import org.jitsi.meet.sdk.*
import java.net.URL

class VideoCallActivity : AppCompatActivity(), JitsiMeetActivityInterface {

    private var view:JitsiMeetView? = null
    private var mApplicantId: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        if(intent.hasExtra(Constants.EXTRA_APPLICATION_ID)) {
            mApplicantId = intent.getStringExtra(Constants.EXTRA_APPLICATION_ID)!!
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirestoreClass().getOtherUserDetails(this, userId)

    }
    fun startCall(user: User) {
        val userInfo = JitsiMeetUserInfo()
        userInfo.displayName = "${user.firstName} ${user.lastName}"
        userInfo.avatar = URL(user.image)

        view = JitsiMeetView(this)
        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(URL("https://meet.jit.si"))
            .setRoom(mApplicantId)
            .setAudioMuted(true)
            .setVideoMuted(false)
            .setAudioOnly(false)
            .setUserInfo(userInfo)
            .build()
        view!!.join(options)
        flVideoCall.addView(view)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        JitsiMeetActivityDelegate.onActivityResult(this,requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        JitsiMeetActivityDelegate.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        view!!.dispose()
        view = null
        JitsiMeetActivityDelegate.onHostDestroy(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        JitsiMeetActivityDelegate.onNewIntent(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
    }


    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {}

    override fun onResume() {
        super.onResume()
        JitsiMeetActivityDelegate.onHostResume(this)
    }

    override fun onStop() {
        super.onStop()
        JitsiMeetActivityDelegate.onHostPause(this)
    }

}
package com.innovaid.furriends.ui.activities.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.RadioButton
import android.widget.Toast
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.UserAdoptionForm
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.activity_add_user_pet_profile.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_user_adoption.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.text.SimpleDateFormat
import java.util.*

class UserAdoptionActivity : BaseActivity() {

    private var mPetId: String = ""
    private var mApplicantId: String = ""
    private var petOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_adoption)


        if(intent.hasExtra(Constants.EXTRA_PET_ID)) {
            mPetId = intent.getStringExtra(Constants.EXTRA_PET_ID)!!
        }
        if(intent.hasExtra(Constants.EXTRA_PET_OWNER_ID)) {
            petOwnerId = intent.getStringExtra(Constants.EXTRA_PET_OWNER_ID)!!
        }


        setupActionBar()

        btnSubmitUserPetAdoptionForm.setOnClickListener {
            if(validateFormCredentials()) {
                submitUserPetAdoptionForm()
            }
        }

    }
    private fun submitUserPetAdoptionForm() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val userQuestionThree = findViewById<RadioButton>(rgUserQuestionThree.checkedRadioButtonId)
        val userQuestionFour = findViewById<RadioButton>(rgUserQuestionFour.checkedRadioButtonId)
        val userQuestionFive = findViewById<RadioButton>(rgUserQuestionFive.checkedRadioButtonId)


        val userAdoptionForm = UserAdoptionForm(
            petOwnerId,
            etUserPetApplicantName.text.toString().trim { it <= ' '},
            etUserPetApplicantContactNumber.text.toString().trim { it <= ' '},
            etUserPetApplicantAddress.text.toString().trim { it <= ' '},

            FirestoreClass().getCurrentUserID(),
            etUserPetApplicantOccupation.text.toString().trim { it <= ' '},
            etUserQuestionOne.text.toString().trim { it <= ' '},
            etUserQuestionTwo.text.toString().trim { it <= ' '},
            userQuestionThree.text.toString().trim { it <= ' '},
            userQuestionFour.text.toString().trim { it <= ' '},
            userQuestionFive.text.toString().trim { it <= ' '},
            generateTimestamp(),
            "Application is being reviewed",
            mPetId,
            "none"
        )

        FirestoreClass().uploadPetAdoptionFormDetails(this, userAdoptionForm)
    }

    fun uploadPetAdoptionFormDetailsSuccess(userAdoptionForm: UserAdoptionForm) {
        hideProgressDialog()
        val petHashMap = HashMap<String, Any>()
        petHashMap[Constants.PET_ADOPTION_STATUS] = "Ongoing"
        FirestoreClass().changePetAdoptionStatus(this, petHashMap, mPetId)
        mApplicantId = userAdoptionForm.applicationId.toString()
    }
    fun changedPetAdoptionStatusSuccess() {
        val intent = Intent(this, PetUserApplicationStatusActivity::class.java)
        intent.putExtra(Constants.EXTRA_APPLICATION_ID,mApplicantId)
        intent.putExtra(Constants.EXTRA_PET_ID, mPetId)
        Toast.makeText(this,"Your adoption form is successfully submitted", Toast.LENGTH_SHORT).show()
        finish()
        startActivity(intent)
    }


    private fun generateTimestamp(): String {
        val current = Calendar.getInstance().time
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return format.format(current)
    }
    private fun validateFormCredentials(): Boolean {
        return when {
            TextUtils.isEmpty(etUserPetApplicantName.text.toString().trim { it <= ' '}) -> {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etUserPetApplicantContactNumber.text.toString().trim { it <= ' '}) -> {
                Toast.makeText(this, "Please enter your contact number", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etUserPetApplicantAddress.text.toString().trim { it <= ' '}) -> {
                Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etUserPetApplicantOccupation.text.toString().trim { it <= ' '}) -> {
                Toast.makeText(this, "Please enter your occupation", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etUserQuestionOne.text.toString().trim { it <= ' '}) -> {
                Toast.makeText(this, "Please answer the first question", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etUserQuestionTwo.text.toString().trim { it <= ' '}) -> {
                Toast.makeText(this, "Please answer the second question", Toast.LENGTH_SHORT).show()
                false
            }
            rgUserQuestionThree.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please answer the third question", Toast.LENGTH_SHORT).show()
                false
            }
            rgUserQuestionFour.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please answer the fourth question", Toast.LENGTH_SHORT).show()
                false
            }
            rgUserQuestionFive.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please answer the fifth question", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(tbUserAdoptionActivity)
        supportActionBar!!.title = "Message"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbUserAdoptionActivity.setNavigationOnClickListener { onBackPressed() }
    }
}
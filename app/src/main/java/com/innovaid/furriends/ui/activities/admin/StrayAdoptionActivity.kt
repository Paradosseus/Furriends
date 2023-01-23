package com.innovaid.furriends.ui.activities.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.innovaid.furriends.R
import com.innovaid.furriends.ViewStrayAdoptionFormActivity
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.user.UserApplicationStatusActivity
import com.innovaid.furriends.utils.Constants
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.pdf.parser.PdfReaderContentParser
import kotlinx.android.synthetic.main.activity_add_user_pet_profile.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_stray_adoption.*
import kotlinx.android.synthetic.main.activity_user_favorites.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class StrayAdoptionActivity : BaseActivity(), View.OnClickListener {

    private var mStrayAnimalFormURL: String = ""
    private var mStrayAnimalId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stray_adoption)

        setupActionBar()

        val assetManager = this.assets
        val inputStream = assetManager.open("Adoption-Form-Interactive.pdf")

        if(intent.hasExtra(Constants.EXTRA_STRAY_ANIMAL_ID)) {
            mStrayAnimalId = intent.getStringExtra(Constants.EXTRA_STRAY_ANIMAL_ID)!!
        }


        tvViewAdoptionForm.setOnClickListener(this)
        btnSubmitAdoptionForm.setOnClickListener(this)


    }
    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.tvViewAdoptionForm -> {
                    val intent = Intent(this, ViewStrayAdoptionFormActivity::class.java)
                    startActivity(intent)
                }
                R.id.btnSubmitAdoptionForm -> {
                    submitAdoptionForm()
                }
            }
        }
    }
    fun getCurrentDateAndTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)

    }


    private fun submitAdoptionForm() {
            if (validateFormCredentials()) {
                val assetManager = this.assets
                val inputStream = assetManager.open("Adoption-Form-Interactive.pdf")
                val reader = PdfReader(inputStream)
                val file = File(this.getExternalFilesDir(null), "filled_form.pdf")
                val pdfUri = Uri.fromFile(file)
                val stamper = PdfStamper(reader, FileOutputStream(file))
                val form = stamper.acroFields

                //Current Date Generator
                val date = Date()
                val format = SimpleDateFormat("yyyy-MM-dd")
                val dateString = format.format(date)
                val parts = dateString.split("-")
                val yearString = parts[0]
                val year = yearString.substring(yearString.length-2)
                val monthString = parts[1]
                val dayString = parts[2]

                form.setField("Name", etUserApplicantName.text.toString())
                form.setField("Barangay-Address", etUserApplicantBarangayAddress.text.toString())
                form.setField("species", spStrayPetType.selectedItem.toString())
                form.setField("breed", etAdoptedStrayBreed.text.toString())
                form.setField("color", etAdoptedStrayColor.text.toString())
                form.setField("gender", spAdoptedStrayGender.selectedItem.toString())
                form.setField("day", dayString)
                form.setField("month", monthString)
                form.setField("year", year)
                form.setField("Name and Signature", etUserApplicantName.text.toString())
                stamper.close()
                reader.close()
                inputStream.close()

                FirestoreClass().uploadStrayAnimalAdoptionToStorage(this, pdfUri)

            }
    }
    fun uploadPdfSuccess(pdfUrl: String) {

        mStrayAnimalFormURL = pdfUrl

        FirestoreClass().getUserInfo(this)
    }

    fun uploadStrayApplicationFormDetails(user: User) {
        val currentDateTime = getCurrentDateAndTime()
        val parts = currentDateTime.split(" ")
        val currentDate = parts[0]
        val currentTime = parts[1]

        val strayAdoptionForm = StrayAdoptionForm(
            "${user.firstName} ${user.lastName}",
            user.address,
            FirestoreClass().getCurrentUserID(),
            currentDate,
            currentTime,
            "application_being_reviewed",
            mStrayAnimalFormURL,
            mStrayAnimalId,
            "none"
        )
        FirestoreClass().uploadStrayAdoptionFormDetails(this@StrayAdoptionActivity, strayAdoptionForm)

    }
    fun uploadStrayApplicationFormDetailsSuccess() {

        Toast.makeText(this,"Your adoption is successfully submitted", Toast.LENGTH_SHORT).show()
        finish()
        startActivity(Intent(this,UserApplicationStatusActivity::class.java))

    }

    private fun validateFormCredentials(): Boolean {
        return when {
            TextUtils.isEmpty(etUserApplicantName.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,"Please enter your name", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etUserApplicantBarangayAddress.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,"Please enter your barangay address", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etUserApplicantName.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,"Please enter your name", Toast.LENGTH_SHORT).show()
                false
            }
            spStrayPetType.selectedItem.toString() == "Please Select" -> {
                Toast.makeText(this, "Please specify the Pet's type", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etAdoptedStrayBreed.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,"Please enter the Pet's breed", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etAdoptedStrayColor.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, "Please enter the Pet's color", Toast.LENGTH_SHORT).show()
                false
            }
            spAdoptedStrayGender.selectedItem.toString() == "Select Gender" -> {
                Toast.makeText(this, "Please specify the Pet's gender", Toast.LENGTH_SHORT).show()
                false
            }
            !cbAdoptionTermsAndCondition.isChecked() -> {
                Toast.makeText(this, "Please check if you agreed to the terms and conditions", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }

    }
    private fun setupActionBar() {

        setSupportActionBar(tbStrayAdoptionActivity)
        supportActionBar!!.title = "Adoption Form"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbStrayAdoptionActivity.setNavigationOnClickListener { onBackPressed() }
    }

}
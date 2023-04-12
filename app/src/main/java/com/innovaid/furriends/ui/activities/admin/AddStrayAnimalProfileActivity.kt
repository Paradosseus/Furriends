package com.innovaid.furriends.ui.activities.admin

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_stray_animal_profile.*
import kotlinx.android.synthetic.main.activity_add_user_pet_profile.*
import kotlinx.android.synthetic.main.activity_set_up_user_profile.*
import kotlinx.android.synthetic.main.activity_set_up_user_profile.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddStrayAnimalProfileActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri: Uri? = null
    private var mStrayAnimalImageURL: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stray_animal_profile)

        ivAddStrayAnimalImage.setOnClickListener(this)
        btnSetupStrayAnimalProfile.setOnClickListener(this)
        ivDateSelector.setOnClickListener(this)
        ivSelectTime.setOnClickListener(this)
        ibASAPBackButton.setOnClickListener(this)

        val rbStrayAnimalVaccinated = findViewById<RadioButton>(R.id.rbSAVSVaccinated)

        rbStrayAnimalVaccinated.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
               llStrayAnimalVaccinationBrand.visibility = View.VISIBLE
            } else {
                llStrayAnimalVaccinationBrand.visibility = View.GONE
            }
        }
    }


    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.ivAddStrayAnimalImage -> {
                    if(ContextCompat.checkSelfPermission(
                            this,android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.imageSelector(this)
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btnSetupStrayAnimalProfile -> {
                    if (validateStrayAnimalDetails()) {

                        uploadStrayAnimalPage()
                    }
                }
                R.id.ivDateSelector -> {
                    setDate()
                }
                R.id.ibASAPBackButton -> {
                    onBackPressed()
                }
                R.id.ivSelectTime -> {
                    setTime()
                }


            }
        }
    }
    private fun setDate() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateTable(myCalendar)
        }
        DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(
            Calendar.DAY_OF_MONTH)).show()
    }
    private fun updateTable(myCalendar: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        tvStrayAnimalDateFoundedValue.setText(sdf.format(myCalendar.time))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission is granted", Toast.LENGTH_SHORT).show()
                Constants.imageSelector(this)
            } else {
                Toast.makeText(this, "You denied the storage permission. Go to settings to allow it.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    mSelectedImageFileUri = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, ivStrayAnimalImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Image Selection Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateStrayAnimalDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
                false
            }
            tvStrayAnimalDateFoundedValue.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter Date when the Stray Animal was found", Toast.LENGTH_SHORT).show()
                false
            }
            tvStrayAnimalTimeFoundedValue.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter Time when the Stray Animal was found", Toast.LENGTH_SHORT).show()
                false
            }
            etStrayAnimalLocationFoundedValue.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter the location where the Stray Animal was found", Toast.LENGTH_SHORT).show()
                false
            }
            etStrayAnimalBreed.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter the Stray Animal's Breed", Toast.LENGTH_SHORT).show()
                false
            }
            etStrayAnimalColor.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter the Stray Animal's Color", Toast.LENGTH_SHORT).show()
                false
            }
            spStrayAnimalPetTypeValue.selectedItem.toString() == "Please Select"-> {
                Toast.makeText(this, "Please specify the Stray Animal's type", Toast.LENGTH_SHORT).show()
                false
            }
            rgStrayAnimalGender.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please specify your Pet's type", Toast.LENGTH_SHORT).show()
                false
            }
            rgStrayAnimalSpayedNeutered.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please specify if your Pet is spayed and neutered or not", Toast.LENGTH_SHORT).show()
                false
            }
            etStrayAnimalDescription.text.toString().trim { it <= ' ' }.isEmpty()-> {
                Toast.makeText(this, "Please Enter your pet's description", Toast.LENGTH_SHORT).show()
                false
            }
            rgStrayAnimalVaccinationStatus.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please specify if your Pet is vaccinated or not", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }

    }
    private fun uploadStrayAnimalDetails() {

        val username = this.getSharedPreferences(Constants.FURRIENDS_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME, "")!!
        val strayAnimalGender = findViewById<RadioButton>(rgStrayAnimalGender.checkedRadioButtonId)
        val strayAnimalSpayedNeutered = findViewById<RadioButton>(rgStrayAnimalSpayedNeutered.checkedRadioButtonId)
        val strayAnimalVaccinationStatus = findViewById<RadioButton>(rgStrayAnimalVaccinationStatus.checkedRadioButtonId)


        val strayAnimal = StrayAnimal(
            FirestoreClass().getCurrentUserID(),
            etStrayAnimalBreed.text.toString().trim { it <= ' ' },
            etStrayAnimalColor.text.toString().trim { it <= ' ' },
            spStrayAnimalPetTypeValue.selectedItem.toString(),
            strayAnimalGender.text.toString().trim { it <= ' ' },
            strayAnimalSpayedNeutered.text.toString().trim { it <= ' ' },
            etStrayAnimalLocationFoundedValue.text.toString().trim { it <= ' ' },
            tvStrayAnimalDateFoundedValue.text.toString().trim { it <= ' ' },
            tvStrayAnimalTimeFoundedValue.text.toString().trim { it <= ' ' },
            strayAnimalVaccinationStatus.text.toString(),
            etStrayAnimalVaccinationBrand.text.toString(),
            etStrayAnimalDescription.text.toString().trim { it <= ' ' },
            mStrayAnimalImageURL,
            "Listed",
            "stray"
        )

        FirestoreClass().uploadStrayAnimalDetails(this@AddStrayAnimalProfileActivity, strayAnimal)
    }

    private fun uploadStrayAnimalPage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(
            this@AddStrayAnimalProfileActivity,
            mSelectedImageFileUri,
            Constants.STRAY_ANIMAL_IMAGE
        )
    }

    fun imageUploadSuccess(imageURL: String) {

        mStrayAnimalImageURL = imageURL

        uploadStrayAnimalDetails()
    }
    fun strayAnimalUploadSuccess() {

        hideProgressDialog()

        Toast.makeText(this, "Stray Animal is uploaded successfully", Toast.LENGTH_SHORT).show()

        finish()
    }
    private fun setTime() {

        val currentTime = Calendar.getInstance()
        val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentTime.get(Calendar.MINUTE)

        val timePickerView = TimePicker(this)
        timePickerView.setOnTimeChangedListener { view, hourOfDay, minute ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Time")
            .setView(timePickerView)
            .setPositiveButton("OK") { _, _ ->
                val hour = timePickerView.hour
                val minute = timePickerView.minute
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                tvStrayAnimalTimeFoundedValue.text = dateFormat.format(calendar.time)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // Set initial time
        timePickerView.hour = startHour
        timePickerView.minute = startMinute

        // Disable minutes and seconds
        try {
            val minuteSpinnerId = Resources.getSystem().getIdentifier("minute", "id", "android")
            val minuteSpinner = timePickerView.findViewById<View>(minuteSpinnerId)
            minuteSpinner?.visibility = View.GONE

            val secondSpinnerId = Resources.getSystem().getIdentifier("second", "id", "android")
            val secondSpinner = timePickerView.findViewById<View>(secondSpinnerId)
            secondSpinner?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Set selectable range
        timePickerView.setIs24HourView(false)
    }
}
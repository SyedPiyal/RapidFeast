package com.piyal.rapidfeast.signup

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels

import androidx.lifecycle.Observer
import com.piyal.rapidfeast.data.local.PreferencesHelper
import com.piyal.rapidfeast.data.local.Resource
import com.piyal.rapidfeast.data.model.PlaceModel
import com.piyal.rapidfeast.data.model.UpdateUserRequest
import com.piyal.rapidfeast.data.model.UserModel
import com.piyal.rapidfeast.databinding.ActivitySignUpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity(), PlacePickerDialog.PlaceClickListener {


    private val viewModel: SignUpViewModel by viewModels()

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var progressDialog: ProgressDialog
    private var places: ArrayList<PlaceModel> = ArrayList()
    private var selectedPlace: PlaceModel? = null
    private var number: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getArgs()
        initView()
        setListener()
        setObservers()
        viewModel.getPlaces()
    }

    private fun getArgs() {
        number = preferencesHelper.mobile
    }

    private fun initView() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonRegister.setOnClickListener {
            if (binding.editName.text.toString().isNotEmpty()) {
                // TODO email validation
                if (binding.editEmail.text.toString().isNotEmpty()) {
                    if (selectedPlace != null) {
                        var token = ""
                        token = preferencesHelper.fcmToken.toString()
                        val updateUserRequest = UpdateUserRequest(
                            placeModel = selectedPlace!!,
                            userModel = UserModel(
                                preferencesHelper.userId,
                                binding.editEmail.text.toString(),
                                number,
                                binding.editName.text.toString(),
                                preferencesHelper.oauthId
                            )
                        )
                        viewModel.signUp(updateUserRequest)
                    } else {
                        Toast.makeText(applicationContext, "Select a place", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Email is blank", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Name is blank", Toast.LENGTH_SHORT).show()
            }
        }
        binding.layoutChooseCampus.setOnClickListener {
            showCampusListBottomDialog()
        }
    }

    private fun setObservers() {
        viewModel.performFetchPlacesList.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Getting places")
                        progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "No places found", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        places.clear()
                        it.data?.data?.let { it1 -> places.addAll(it1) }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "Something went wrong", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                }
            }
        })

        viewModel.performSignUp.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        preferencesHelper.name = binding.editName.text.toString()
                        preferencesHelper.email = binding.editEmail.text.toString()
                        preferencesHelper.place = Gson().toJson(selectedPlace)
                        progressDialog.dismiss()
                        if (resource.data != null) {
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Logging in...")
                        progressDialog.show()
                    }

                    else -> {}
                }
            }
        })
    }

    private fun showCampusListBottomDialog() {
        viewModel.searchPlace("")
        val dialog = PlacePickerDialog()
        dialog.setListener(this)
        dialog.placesList = places
        dialog.show(supportFragmentManager, null)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@SignUpActivity)
            .setTitle("Cancel process?")
            .setMessage("Are you sure want to cancel the registration process?")
            .setPositiveButton("Yes") { dialog, which ->
                super.onBackPressed()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .show()
    }

    override fun onPlaceClick(place: PlaceModel) {
        selectedPlace = place
        binding.textCampusName.text = place.name
    }
}

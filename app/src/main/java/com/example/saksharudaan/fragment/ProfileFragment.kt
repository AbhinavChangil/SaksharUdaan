package com.example.saksharudaan.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.saksharudaan.EditProfileActivity
import com.example.saksharudaan.R
import com.example.saksharudaan.databinding.FragmentProfileBinding
import com.example.saksharudaan.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var dialog: Dialog
    private lateinit var profileImageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        //initialize auth and database variables
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize loading dialog
        dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.loading_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
        dialog.show()


        //update profile name, image and email
        setUserData()

        //edit profile
        binding.cvEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }


        return binding.root
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = auth.currentUser?.uid?:""
            val userReference = database.getReference("user").child(userId).child("profile")

            // Set all values
            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if (userProfile != null) {
                            binding.apply {
                                tvNameProfile.text = userProfile.name
                                tvEmailProfile.text = userProfile.email
                                profileImageUri = userProfile.imageUri.toString()
                                dialog.dismiss()
                                if(profileImageUri!=""){
                                    val uri = Uri.parse(userProfile.imageUri)
                                    Glide.with(requireContext())
                                        .load(uri)
                                        .into(imgProfile)
                                }else{
                                    imgProfile.setImageResource(R.drawable.ic_profile_img_foreground)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Failed to load user data")
                }
            })
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
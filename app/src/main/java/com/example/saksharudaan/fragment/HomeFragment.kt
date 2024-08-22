package com.example.saksharudaan.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.saksharudaan.Adapter.CourseAdapter
import com.example.saksharudaan.R
import com.example.saksharudaan.databinding.FragmentHomeBinding
import com.example.saksharudaan.model.CourseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var loadingDialog: Dialog
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var courseList: ArrayList<CourseModel>
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentHomeBinding.inflate(inflater, container, false)

        //initialize firebase auth and database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize Loading Dialog
        loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(R.layout.loading_dialog)

        if (loadingDialog.window != null) {

            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
        }

        retrieveAndDisplayCourses()


        return binding.root
    }

    private fun retrieveAndDisplayCourses() {
        loadingDialog.show()
        val courseRef =  database.reference.child("course")
        courseList = arrayListOf()
        courseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                courseList.clear()
                for(courseSnapshot in snapshot.children){
                    val courseItem = courseSnapshot.getValue(CourseModel::class.java)
                    courseItem?.let {
                        courseList.add(it)
                    }
                }

                // Shuffle the course list
                courseList.shuffle()

                // Get a sublist with the first 2 items or the full list if it has less than 2 items
                val randomCourses = if (courseList.size > 1) courseList.subList(0, 1) else courseList

                setAdapter(randomCourses)
                loadingDialog.dismiss()
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }

        })
    }

    private fun setAdapter(courseList: MutableList<CourseModel>) {
        courseAdapter = CourseAdapter(requireContext(), courseList)
        binding.rvHome.layoutManager = GridLayoutManager(context,2)
        binding.rvHome.adapter = courseAdapter
        courseAdapter.notifyDataSetChanged()
    }

}
package com.example.saksharudaan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.saksharudaan.databinding.ActivityMainBinding
import com.example.saksharudaan.fragment.HomeFragment
import com.example.saksharudaan.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)

        //Initialize with home fragment
        binding.bottomNavBar.setItemSelected(R.id.home,true)
        binding.tvToolbarTilte.text = "SaksharUdaan"
        supportFragmentManager.beginTransaction().replace(R.id.container_main_ll, HomeFragment()).commit()

        // Set up the ChipNavigationBar
        setupChipNavigationBar()

    }

    private fun setupChipNavigationBar() {
        binding.bottomNavBar.setOnItemSelectedListener { itemId ->
            var selectedFragment: Fragment? = null
            when (itemId) {
                R.id.home -> { selectedFragment = HomeFragment()
                binding.tvToolbarTilte.text = "SaksharUdaan"
                }
                R.id.profile -> {
                    selectedFragment = ProfileFragment()
                    binding.tvToolbarTilte.text = "Profile"
                }
//                R.id.add -> {
//                    startActivity(Intent(this, LoginActivity::class.java))
//                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.container_main_ll, selectedFragment).commit()
            }
        }
    }
}


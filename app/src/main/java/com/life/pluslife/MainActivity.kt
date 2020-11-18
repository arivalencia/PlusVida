package com.life.pluslife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.analytics.FirebaseAnalytics
import com.life.pluslife.R
import com.life.pluslife.fragments.HelpFragment
import com.life.pluslife.fragments.HomeFragment
import com.life.pluslife.fragments.OptionsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

    val homeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val helpFragment = HelpFragment()
        val optionsFragment = OptionsFragment()

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.home -> {
                    setUpFragment( homeFragment )
                    true
                }
                R.id.help -> {
                    setUpFragment( helpFragment )
                    true
                }
                R.id.options -> {
                    setUpFragment( optionsFragment )
                    true
                }
                else -> false
            }
        }
        bottom_navigation.selectedItemId = R.id.home

        /*val analytics = FirebaseAnalytics.getInstance(this)
        val data = Bundle()
        data.putString("Message", "run")
        analytics.logEvent("InitAPP", data )*/

    }

    private fun setUpFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {

        if ( homeFragment.isVisible ) { finish() }
        else { super.onBackPressed() }

    }
}
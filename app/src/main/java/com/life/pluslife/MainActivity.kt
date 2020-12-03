package com.life.pluslife

import android.os.Bundle
import android.util.Log
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

    val TAG = "MainActivity"
    val helpFragment = HelpFragment()
    var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
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
        bottom_navigation.selectedItemId = R.id.help

        /*val analytics = FirebaseAnalytics.getInstance(this)
        val data = Bundle()
        data.putString("Message", "run")
        analytics.logEvent("InitAPP", data )*/

    }

    private fun setUpFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        if ( currentFragment == null ) {
            transaction.add(R.id.frame_container, fragment)
        } else {
            if (fragment.isAdded) {
                transaction
                    .hide(currentFragment!!)
                    .show(fragment)
            } else {
                transaction
                    .hide(currentFragment!!)
                    .add(R.id.frame_container, fragment)
            }
        }
        transaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            //.addToBackStack(null)
            .commitAllowingStateLoss()

        currentFragment = fragment
    }

    override fun onBackPressed() {
        if ( helpFragment.isVisible ) { finish() }
        else { super.onBackPressed() }
    }
}
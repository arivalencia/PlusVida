package com.life.pluslife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.life.pluslife.fragments.HelpFragment
import com.life.pluslife.fragments.HomeFragment
import com.life.pluslife.fragments.OptionsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

    val TAG = "MainActivity"
    val helpFragment = HelpFragment()
    var currentFragment: Fragment? = null
    var lastFragmentTag: String = ""
    var currentFragmentTag: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val optionsFragment = OptionsFragment()

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.home -> {
                    setUpFragment( homeFragment, "home")
                    true
                }
                R.id.help -> {
                    setUpFragment( helpFragment, "help")
                    true
                }
                R.id.options -> {
                    setUpFragment( optionsFragment, "options")
                    true
                }
                else -> false
            }
        }
        bottom_navigation.selectedItemId = R.id.help

    }

    private fun setUpFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        if ( currentFragment == null ) {
            transaction.add(R.id.frame_container, fragment, tag)
        } else {
            if (fragment.isAdded) {
                transaction
                    .hide(currentFragment!!)
                    .show(fragment)
            } else {
                transaction
                    .hide(currentFragment!!)
                    .add(R.id.frame_container, fragment, tag)
            }
        }
        transaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commitAllowingStateLoss()


        lastFragmentTag = currentFragmentTag
        currentFragmentTag = tag
        currentFragment = fragment
    }

    override fun onBackPressed() {

        /*if ( lastFragmentTag.equals("") ){
            super.onBackPressed()
        } else {
            val currentFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
            val oldFragment = supportFragmentManager.findFragmentByTag(lastFragmentTag)

            if (currentFragment!!.isVisible && oldFragment!!.isHidden) {
                supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .hide(currentFragment)
                    .show(oldFragment)
                    .commit()
            }
            lastFragmentTag = ""
        }*/

        if ( helpFragment.isVisible ) { finish() }
        else { super.onBackPressed() }
    }
}
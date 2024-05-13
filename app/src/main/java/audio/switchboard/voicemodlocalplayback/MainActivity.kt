package audio.switchboard.voicemodlocalplayback

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import audio.switchboard.voicemodlocalplayback.databinding.ActivityMainBinding
import audio.switchboard.voicemodlocalplayback.utils.ContextHolder
import audio.switchboard.voicemodlocalplayback.utils.ExampleProvider
import com.synervoz.switchboard.sdk.logger.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ExampleProvider.initialize(this)
        Logger.init()
        ContextHolder.activity = this
        if (!requestPermission()) return
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace<MainFragment>(R.id.container, MainFragment.TAG)
                setReorderingAllowed(true)
            }
        }
    }

    fun pushFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            add(R.id.container, fragment, fragment.javaClass.name)
            setReorderingAllowed(true)
            addToBackStack(fragment.javaClass.name)
        }
        // Avoid clicks to propagate from the top fragment to the bottom MainFragment
        supportFragmentManager.findFragmentByTag(MainFragment.TAG)?.view?.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != 0 || grantResults.isEmpty() || grantResults.size != permissions.size) return
        var hasAllPermissions = true

        for (grantResult in grantResults)
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false
                Toast.makeText(
                    applicationContext,
                    "Please allow all permissions for the app.",
                    Toast.LENGTH_LONG
                ).show()
            }
        if (hasAllPermissions) {
            supportFragmentManager.commit {
                replace<MainFragment>(R.id.container)
                setReorderingAllowed(true)
            }
        }
    }

    private fun requestPermission(): Boolean {
        val permissions: MutableList<String> = mutableListOf(
            Manifest.permission.RECORD_AUDIO
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 0)
                return false
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        supportFragmentManager.popBackStackImmediate()
        val fragments = supportFragmentManager.fragments
        if (fragments.isNotEmpty()) {
            val lastFragment = supportFragmentManager.fragments.last()
            if (lastFragment.tag == MainFragment.TAG) {
                // Show the MainFragment if all the other fragments were removed
                supportFragmentManager.findFragmentByTag(MainFragment.TAG)?.view?.visibility = View.VISIBLE
            }
        }
    }
}
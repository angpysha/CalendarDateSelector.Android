package io.github.angpysha.extendedcontrols.testcalendar

import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.SingleDateSelector
import io.github.angpysha.calendardateselector.CalendarDateSelector
import io.github.angpysha.calendardateselector.ESelectionType
import io.github.angpysha.calendardateselector.Enums.EStartWeekType
import io.github.angpysha.calendardateselector.RangeDateSelectedResult
import io.github.angpysha.calendardateselector.SingleDateSelectedResult
import io.github.angpysha.extendedcontrols.testcalendar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

       // val navController = findNavController(R.id.nav_host_fragment_content_main)
      //  appBarConfiguration = AppBarConfiguration(navController.graph)
      //  setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val cal = findViewById<CalendarDateSelector>(R.id.calendar_date_selector1)
        cal.selectionMode = ESelectionType.Range
//        cal.startWeekType = EStartWeekType.Sunday
        cal.setOnItemClickedListener {
            val ioo = 0
            when (it) {
                is SingleDateSelectedResult -> {

                }
                is RangeDateSelectedResult -> {
                    val ffds = 0
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
     //   val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
        return false
    }
}
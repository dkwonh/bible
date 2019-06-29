package com.example.bible

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import androidx.core.util.keyIterator
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class LinePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var recyclerView: RecyclerView
    private val dbHelper = DBHelper(this)
    var selectedLine = SparseArray<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.line_page_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        val fab : FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            if(selectedLine.isNotEmpty()) {
                val date = Date(System.currentTimeMillis())
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                simpleDateFormat.format(date)
                var str = ""
                selectedLine.forEach {
                        key, value -> str += value
                }
                dbHelper.addMemo(simpleDateFormat.format(date), str)
            }
            else
                Toast.makeText(this,"절을 선택하고 저장버튼을 누르시면 메모할 수 있어요",Toast.LENGTH_LONG).show()
        }


        recyclerView = findViewById(R.id.line_recycler)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val text = dbHelper.getContents(intent.getIntExtra("Num",1001001))
        val line = intent.getIntExtra("lineNum",1)%1000

        navView.setNavigationItemSelectedListener(this)

        val lineAdapter = LineAdapter(this,text)

        selectedLine = lineAdapter.selected

        recyclerView.adapter = lineAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        recyclerView.scrollToPosition(line-1)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this,MemoPage::class.java)
                startActivity(intent)
            }
            R.id.nav_gallery -> {
                dbHelper.deleteAllMemo()
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
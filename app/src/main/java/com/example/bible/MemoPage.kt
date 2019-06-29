package com.example.bible

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.forEach
import androidx.core.util.valueIterator
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MemoPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var recyclerView: RecyclerView
    var dbHelper = DBHelper(this)
    var selectedMemo = SparseArray<String>(0)
    lateinit var memoAdapter : MemoAdapter
    var memoList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memo_page_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )



        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val id = arrayListOf<String>()
            selectedMemo.forEach{
                key, values -> id.add(values.substring(0,19))
                //dbHelper.testSelectMemo(values.substring(0,19))
                dbHelper.deleteMemo(values.substring(0,19))
                memoList.remove(values)

                memoAdapter.booleanArray.clear()
                memoAdapter.notifyDataSetChanged()

            }
        }

        memoList = dbHelper.getAllMemo()

        memoAdapter = MemoAdapter(
            this,
            memoList
            ){ _, _->  }


        recyclerView = findViewById(R.id.recycler_memo)

        recyclerView.adapter = memoAdapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setHasFixedSize(true)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        selectedMemo = memoAdapter.selected

        navView.setNavigationItemSelectedListener(this)


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
            }
            R.id.nav_gallery -> {
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
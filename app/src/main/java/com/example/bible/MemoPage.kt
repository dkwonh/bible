package com.example.bible

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.forEach
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
    var selectedMemoId = SparseArray<String>(0)
    lateinit var memoAdapter: MemoAdapter
    var memoList = HashMap<String, String>()
    private val memo = arrayListOf<String>()
    private val memoId = arrayListOf<String>()

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
            selectedMemo.forEach { _, values ->
                memo.remove(values)
            }
            selectedMemoId.forEach { _, values ->
                dbHelper.deleteMemo(values)
                memoId.remove(values)
            }
            memoAdapter.booleanArray.clear()
            memoAdapter.notifyDataSetChanged()
        }

        memoList = dbHelper.getAllMemo()

        for (key in memoList.keys) {
            memo.add(memoList[key].toString())
            memoId.add(key)
        }

        memoAdapter = MemoAdapter(
            this,
            memo, memoId
        ) { id, memo, _ ->
            intent = Intent(this, MemoEdit::class.java)
            intent.putExtra("MEMO", memo)
            intent.putExtra("ID", id)
            startActivity(intent)
        }


        recyclerView = findViewById(R.id.recycler_memo)

        recyclerView.adapter = memoAdapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setHasFixedSize(true)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        selectedMemo = memoAdapter.selectedStr
        selectedMemoId = memoAdapter.selectedId

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
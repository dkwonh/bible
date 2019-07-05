package com.example.bible

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.containsValue
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.*
import kotlin.collections.HashMap

class MemoPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var recyclerView: RecyclerView
    var dbHelper = DBHelper(this)
    var selectedMemo = SparseArray<String>(0)
    var selectedMemoId = SparseArray<String>(0)
    lateinit var memoAdapter: MemoAdapter
    var memoList = HashMap<String, String>()
    private val memo = arrayListOf<String>()
    private val memoId = arrayListOf<String>()
    lateinit var fab: FloatingActionButton
    private val booleanArray = SparseBooleanArray(0)
    var longClickFlag = 0

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

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (selectedMemo.isNotEmpty()) {
                selectedMemo.forEach { _, values ->
                    memo.remove(values)
                }
                selectedMemoId.forEach { _, values ->
                    dbHelper.deleteMemo(values)
                    memoId.remove(values)
                }
                selectedMemo.clear()
                memoAdapter.booleanArray.clear()
                memoAdapter.notifyDataSetChanged()
                changeFabImage(1)
            } else {
                val intent = Intent(this, MemoEdit::class.java)
                intent.putExtra("MemoPage", "Memo")
                startActivityForResult(intent, 2)
            }
        }

        memoUpdate()
        memoAdapter = MemoAdapter(
            this,
            memo, memoId, ItemClick = { id, memo, _ ->
                intent = Intent(this, MemoView::class.java)
                intent.putExtra("MEMO", memo)
                intent.putExtra("ID", id)
                startActivity(intent)
            }
        )
        { id, memo, position, view ->
            changeFabImage(0)
            if (selectedMemo[position] == memo && selectedMemoId[position] == id) {
                booleanArray.put(position, false)
                selectedMemo.remove(position)
                selectedMemoId.remove(position)
                view.setBackgroundColor(Color.rgb(243, 243, 243))
            } else {
                booleanArray.put(position, true)
                selectedMemo.put(position, memo)
                selectedMemoId.put(position, id)
                view.setBackgroundColor(Color.CYAN)
            }
            if(!booleanArray.containsValue(true))
                changeFabImage(1)
            else
                changeFabImage(0)
            true
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

    private fun changeFabImage(flag: Int) {
        when (flag) {
            0 -> fab.setImageResource(R.drawable.ic_delete_dark)
            1 -> fab.setImageResource(R.drawable.ic_notepad_dark)
        }
    }

    override fun onResume() {
        super.onResume()
        memoUpdate()
        memoAdapter.notifyDataSetChanged()
    }

    private fun memoUpdate() {
        memoList = dbHelper.getAllMemo()
        val treeMap = TreeMap<String, String>(reverseOrder())
        treeMap.putAll(memoList)
        memo.clear()
        memoId.clear()
        for (key in treeMap.keys) {
            memo.add(treeMap[key].toString())
            memoId.add(key)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            Log.v("RESULT_OK", requestCode.toString())
            when (requestCode) {
                1 -> {
                    memoUpdate()
                    memoAdapter.notifyDataSetChanged()
                }
                2 -> {
                    memoUpdate()
                    memoAdapter.notifyDataSetChanged()
                }

            }
        }
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
            R.id.nav_note -> {
            }

            R.id.nav_daily -> {
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
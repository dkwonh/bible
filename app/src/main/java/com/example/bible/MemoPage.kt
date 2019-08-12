package com.example.bible

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
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
    private lateinit var recyclerView: RecyclerView
    private var dbHelper = DBHelper(this)
    private var selectedMemo = SparseArray<String>(0)
    private var selectedMemoId = SparseArray<String>(0)
    private lateinit var memoAdapter: MemoAdapter
    private var memoList = HashMap<String, String>()
    private val memo = arrayListOf<String>()
    private val memoId = arrayListOf<String>()
    private lateinit var fab: FloatingActionButton

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
                alertDialog()
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
                selectedMemoClear()
                changeFabImage(1)
            }
        )
        { id, memo, position, view ->
            changeFabImage(0)
            val memoText = view.findViewById<TextView>(R.id.memo)
            val dateText = view.findViewById<TextView>(R.id.date)
            if (selectedMemo[position] == memo && selectedMemoId[position] == id) {
                memoAdapter.booleanArray.put(position, false)
                selectedMemo.remove(position)
                selectedMemoId.remove(position)
                memoText.setBackgroundResource(R.drawable.item_selector)
                dateText.setBackgroundResource(R.drawable.item_bottom_selector)
            } else {
                memoAdapter.booleanArray.put(position, true)
                selectedMemo.put(position, memo)
                selectedMemoId.put(position, id)
                memoText.setBackgroundResource(R.drawable.item_selector_choice)
                dateText.setBackgroundResource(R.drawable.item_bottom_selector_choice)
            }
            if (!memoAdapter.booleanArray.containsValue(true))
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

    private fun selectedMemoClear() {
        selectedMemo.clear()
        memoAdapter.booleanArray.clear()
        memoAdapter.notifyDataSetChanged()
    }

    private fun deleteButton() {
        selectedMemoClear()
        changeFabImage(1)
        Toast.makeText(this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun changeFabImage(flag: Int) {
        when (flag) {
            0 -> fab.setImageResource(R.drawable.ic_delete_dark)
            1 -> fab.setImageResource(R.drawable.ic_notepad_dark)
        }
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
        val v = findViewById<TextView>(R.id.memo_serve)
        when(memo.isEmpty()){
            true -> v.visibility = View.VISIBLE
            false -> v.visibility = View.INVISIBLE}

    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("메모 삭제")
        builder.setMessage("삭제하시겠습니까?")
        builder.setPositiveButton("예") { _, _ -> deleteButton() }
        builder.setNegativeButton("아니오") { _, _ -> }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        memoUpdate()
        memoAdapter.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
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
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.close_app -> {
                alertDialogApp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            R.id.nav_note -> {
            }

            R.id.nav_daily -> {
                val intent = Intent(this, LinePage::class.java)
                intent.putExtra("PROVERBS", "200")
                startActivity(intent)
                finish()
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        when (drawerLayout.isDrawerOpen((GravityCompat.START))) {
            true -> drawerLayout.closeDrawer(GravityCompat.START)
            false -> {
                super.onBackPressed()
            }
        }
    }

    private fun alertDialogApp() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("개역 한글 성경")
        builder.setMessage("종료하실래요?")
        builder.setPositiveButton("예") { _, _ -> ActivityCompat.finishAffinity(this) }
        builder.setNegativeButton("아니오") { _, _ -> }
        builder.show()
    }
}
package com.example.bible

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_linepage.*
import java.util.*

class LinePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var lineAdapter: LineAdapter
    private val dbHelper = DBHelper(this)
    private var selectedLine = SparseArray<String>()
    private var pageId: Int = 1
    private var text = arrayListOf<String>()
    private var pName = String()
    private var pNum = 1
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

        val nextFab = findViewById<FloatingActionButton>(R.id.fab)
        val prevFab = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        prevFab.setOnClickListener { prevPageUpdate() }
        nextFab.setOnClickListener { nextPageUpdate() }

        recyclerView = findViewById(R.id.line_recycler)

        val id = intent.getStringExtra("PROVERBS")
        when (id is String) {
            true -> {
                val date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                if (date > 30)
                    pageIdUpdate(200001000)
                else
                    pageIdUpdate("200${date}000".toInt())
                prevFab.hide()
                nextFab.hide()
            }
            else -> {
                pageIdUpdate(intent.getIntExtra("Num", 1001001))
            }

        }

        text = dbHelper.getContents(pageId)
        val line = intent.getIntExtra("lineNum", 1) % 1000


        lineAdapter = LineAdapter(this, text)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


        selectedLine = lineAdapter.selected

        recyclerView.adapter = lineAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        recyclerView.scrollToPosition(line - 1)

        val bundle = intent.extras
        pName = bundle?.getString("Part") ?: "Title"
        pNum = bundle?.getInt("Number") ?: 1

    }

    private fun pageIdUpdate(i: Int) {
        pageId = i
    }

    private fun prevPageUpdate() {
        var temp = pageId
        temp /= 1000
        temp %= 1000


        if (temp == 1)
        else {
            pNum -= 1
            toolbar.title = "$pName ${pNum}장"
            pageId /= 1000
            pageId -= 1
            pageId *= 1000
            text = dbHelper.getContents(pageId)
            lineAdapter = LineAdapter(this, text)
            recyclerView.adapter = lineAdapter

            selectedLineUpdate()
        }

    }

    private fun nextPageUpdate() {
        var temp = pageId

        temp /= 1000
        temp += 1
        temp *= 1000
        text = dbHelper.getContents(temp)
        if (text.isEmpty())
        else {
            pNum += 1
            pageId = temp
            lineAdapter = LineAdapter(this, text)
            recyclerView.adapter = lineAdapter
            toolbar.title = "$pName ${pNum}장"

            selectedLineUpdate()

        }

    }

    private fun selectedLineUpdate() {
        lineAdapter.selected.clear()
        lineAdapter.booleanArray.clear()
        selectedLine = lineAdapter.selected

        lineAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.line_menu_main, menu)
        toolbar.title =
            intent.getStringExtra("TITLE") ?: "매일 매일 잠언 ${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}장"
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.close_app ->{
                alertDialog()
                true
            }
            R.id.save_memo -> {
                if (selectedLine.isNotEmpty()) {
                    var str = ""
                    selectedLine.forEach { _, value ->
                        str += value
                    }
                    val intent = Intent(this, MemoEdit::class.java).putExtra("MEMO", str)
                    startActivity(intent)

                    lineAdapter.selected.clear()
                    lineAdapter.booleanArray.clear()
                    lineAdapter.notifyDataSetChanged()
                } else
                    Toast.makeText(this, "절을 선택하고 저장버튼을 누르시면 메모할 수 있어요", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)


        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            R.id.nav_note -> {
                val intent = Intent(this, MemoPage::class.java)
                startActivity(intent)
                finish()
            }

            R.id.nav_daily -> {
                if(intent.getStringExtra("PROVERBS") is String)
                else {
                    val intent = Intent(this, LinePage::class.java)
                    intent.putExtra("PROVERBS", "200")
                    startActivity(intent)
                    finish()
                }
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

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("개역 한글 성경")
        builder.setMessage("종료하실래요?")
        builder.setPositiveButton("예") { _, _ -> ActivityCompat.finishAffinity(this) }
        builder.setNegativeButton("아니오") { _, _ -> }
        builder.show()
    }
}
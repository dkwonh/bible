package com.example.bible

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mAdapter: MenuAdapter
    private lateinit var pageAdapter: MenuAdapter
    private lateinit var lineAdapter: MenuAdapter
    private lateinit var recyclerView: RecyclerView
    private val dbHelper = DBHelper(this)
    private var currentList = arrayListOf<String>()
    private var menuList = arrayListOf<String>()
    private var menuListNew = arrayListOf<String>()
    private var depth = 0
    private var currentTitle = "구약 성경"
    //구약 39 신약 27
    private var koPartName = """창세기
출애굽기
레위기
민수기
신명기
여호수아
사사기
룻기
사무엘 상
사무엘 하
열왕기 상
열왕기 하
역대상
역대하
에스라
느헤미야
에스더
욥기
시편
잠언
전도서
아가
이사야
예레미야
예레미야애가
에스겔
다니엘
호세아
요엘
아모스
오바댜
요나
미가
나훔
하박국
스바냐
학개
스가랴
말라기
마태 복음
마가 복음
누가 복음
요한 복음
사도행전
로마서
고린도전서
고린도후서
갈라디아서
에베소서
빌립보서
골로새서
데살로니가전서
데살로니가후서
디모데전서
디모데후서
디도서
빌레몬서
히브리서
야고보서
베드로전서
베드로후서
요한1서
요한2서
요한3서
유다
요한계시록"""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        recyclerView = findViewById(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(this, 3)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        if (!getDatabasePath("bible").exists())
            dbCopy()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            when (currentList[0] == "창세기") {
                true -> {
                    currentList.clear()
                    currentList.addAll(menuListNew)
                    currentTitle = "신약 성경"
                    toolbar.title = currentTitle
                    //fab.setImageResource(R.drawable.ic_old_tes)

                }
                false -> {
                    currentList.clear()
                    currentList.addAll(menuList)
                    currentTitle = "구약 성경"
                    toolbar.title = currentTitle
                    //fab.setImageResource(R.drawable.ic_new_tes)
                }
            }
            depth = 0
            recyclerView.adapter = mAdapter

        }

        var r = 1
        for (i in koPartName.split("""\n""".toRegex())) {
            if (r <= 39) {
                menuList.add(i)
                currentList.add(i)
                r++
            } else
                menuListNew.add(i)
        }

        mAdapter = MenuAdapter(this, currentList) { menu ->
            val pageList = arrayListOf<String>()
            val num = when (currentList[0] == "창세기") {
                true -> menu.toInt()
                false -> menu.toInt() + 39
            }
            for (i in 1..dbHelper.pageCalculator(num)) {
                pageList.add("${i}장")
            }
            depth++
            toolbar.title = currentList[menu.toInt() - 1]
            pageAdapter = MenuAdapter(this, pageList) { page ->
                val lineList = arrayListOf<String>()
                for (i in 1..dbHelper.lineCalculator(num, page.toInt())) {
                    lineList.add("${i}절")
                }
                depth++
                currentTitle = "${currentList[menu.toInt() - 1]} ${page.toInt()}장"
                toolbar.title = currentTitle
                lineAdapter = MenuAdapter(this, lineList) { line ->
                    val intent = Intent(this, LinePage::class.java)
                    intent.putExtra("Num", num * 1000000 + page.toInt() * 1000)
                    intent.putExtra("lineNum", num * 1000000 + page.toInt() * 1000 + line.toInt())
                    intent.putExtra("TITLE", currentTitle)
                    val bundle = Bundle()
                    bundle.putString("Part", currentList[menu.toInt() - 1])
                    bundle.putInt("Number", page.toInt())
                    intent.putExtras(bundle)
                    startActivity(intent)

                }
                recyclerView.adapter = lineAdapter
            }
            recyclerView.adapter = pageAdapter
        }
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        toolbar.title = currentTitle

    }

    override fun onBackPressed() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        when (drawerLayout.isDrawerOpen((GravityCompat.START))) {
            true -> drawerLayout.closeDrawer(GravityCompat.START)
            false -> {
                when (depth) {
                    1 -> {
                        recyclerView.adapter = mAdapter
                        depth--
                        when (currentList[0] == "창세기") {
                            true -> titleUpdate("구약 성경")
                            false -> titleUpdate("신약 성경")
                        }
                        toolbar.title = currentTitle
                    }

                    2 -> {
                        recyclerView.adapter = pageAdapter
                        depth--
                        toolbar.title = currentTitle.replace("""[0-9].*${'$'}""".toRegex(), "")
                    }
                    else -> {
                        alertDialog()
                    }
                }
            }
        }


        /*if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (depth == 1) {
            recyclerView.adapter = mAdapter
            depth--
            when (currentList[0] == "창세기") {
                true -> currentTitle = "구약 성경"
                false -> currentTitle = "신약 성경"
            }
            toolbar.title = currentTitle
        } else if (depth == 2) {
            recyclerView.adapter = pageAdapter
            depth--
            toolbar.title = currentTitle.replace("""[0-9].*${'$'}""".toRegex(), "")
        } else {
            alertDialog()
        }*/
    }

    private fun titleUpdate(title: String) {
        currentTitle = title
        toolbar.title = currentTitle
    }

    private fun dbCopy() {
        FileOutputStream(getDatabasePath("bible").path).use { out ->
            assets.open("bible").use {
                it.copyTo(out)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        toolbar.title = currentTitle
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.close_app -> {
                alertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                depth = 0
                currentList.clear()
                currentList.addAll(menuList)
                mAdapter.notifyDataSetChanged()
                recyclerView.adapter = mAdapter
                toolbar.title = "구약 성경"
                //fab.setImageResource(R.drawable.ic_new_tes)
            }
            R.id.nav_note -> {
                val intent = Intent(this, MemoPage::class.java)
                startActivity(intent)
            }

            R.id.nav_daily -> {
                val intent = Intent(this, LinePage::class.java)
                intent.putExtra("PROVERBS", "200")
                startActivity(intent)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("개역 한글 성경")
        builder.setMessage("종료하실래요?")
        builder.setPositiveButton("예") { _, _ -> finish() }
        builder.setNegativeButton("아니오") { _, _ -> }
        builder.show()
    }
}

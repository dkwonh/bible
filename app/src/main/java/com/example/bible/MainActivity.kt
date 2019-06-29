package com.example.bible

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.FileOutputStream


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mAdapter: MenuAdapter
    private lateinit var pageAdapter: MenuAdapter
    private lateinit var lineAdapter: MenuAdapter
    lateinit var recyclerView: RecyclerView
    private val parser = Parser()
    private val dbHelper = DBHelper(this)
    private var currentList = arrayListOf<String>()
    private var menuList = arrayListOf<String>()
    private var menuListNew = arrayListOf<String>()
    private var depth = 0
    private var currentTitle = "신약 성경"
    //구약 39 신약 27
    private var koPartName = """창세기 (창세,창)
출애굽기/탈출기 (탈출,출)
레위기 (레위,레)
민수기 (민수,민)
신명기 (신명,신)
여호수아 (수)
사사기/판관기 (삿)
룻기 (룻)
사무엘 상 (삼상)
사무엘 하 (삼하)
열왕기 상 (왕상)
열왕기 하 (왕하)
역대상/역대기 상 (대상)
역대하/역대기 하 (대하)
에스라/에즈라 (스)
느헤미야 (느)
에스더/에스테르(에)
욥기 (욥)
시편 (시)
잠언 (잠)
전도서/코헬렛 (전)
아가 (아)
이사야 (사)
예레미야 (렘)
예레미야애가/애가 (애)
에스겔/에제키엘 (겔)
다니엘 (단)
호세아 (호)
요엘 (욜)
아모스 (암)
오바댜 (옵)
요나 (욘)
미가/미카 (미)
나훔 (나)
하박국/하바쿡 (합)
스바냐/스바니야 (습)
학개/하까이 (학)
스가랴/즈카르야 (슥)
말라기/말라키 (말)
마태 복음,마태오 복음(마)
마가 복음,마르코 복음(막)
누가 복음,루카 복음(눅)
요한 복음(요)
사도행전(행)
로마서(롬)
고린도전서,코린토1서(고전)
고린도후서,코린토2서(고후)
갈라디아서(갈)
에베소서, 에페소서(엡)
빌립보서,필리피서(빌)
골로새서,콜로새서(골)
데살로니가전서,테살로니카1서(살전)
데살로니가후서,테살로니카2서(살후)
디모데전서,티모테오1서(딤전)
디모데후서,티모테오2서(딤후)
디도서,티토서(딛)
빌레몬서,필레몬서(몬)
히브리서(히)
야고보서(약)
베드로전서(벧전)
베드로후서(벧후)
요한1서(요일)
요한2서(요이)
요한3서(요삼)
유다서(유)
요한계시록,요한묵시록(계)"""

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

        if(getDatabasePath("bible").exists()){
        }
        else
        {
            FileOutputStream(getDatabasePath("bible").path).use { out ->
                assets.open("bible").use {
                    it.copyTo(out)
                }
            }
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            when (currentList[0] == "창세기 ") {
                true -> {
                    currentList.clear()
                    currentList.addAll(menuListNew)
                    currentTitle = "구약 성경"
                    toolbar.title = currentTitle
                }
                false -> {
                    currentList.clear()
                    currentList.addAll(menuList)
                    currentTitle = "신약 성경"
                    toolbar.title = currentTitle
                }
            }
            depth = 0
            recyclerView.adapter = mAdapter

        }

        var r = 1
        for (i in koPartName.split("""\n""".toRegex())) {
            if (r <= 39) {
                menuList.add(i.replace("""\,.*$|\/.*$|\([가-힣\,]*\)""".toRegex(), ""))
                currentList.add(i.replace("""\,.*$|\/.*$|\([가-힣\,]*\)""".toRegex(), ""))
                r++
            } else
                menuListNew.add(i.replace("""\,.*$|\/.*$|\([가-힣\,]*\)""".toRegex(), ""))
        }

        mAdapter = MenuAdapter(this, currentList) { menu ->
            val pageList = arrayListOf<String>()
            val num = when (currentList[0] == "창세기 ") {
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

    private fun textUpdate(str: String) {
    }

    override fun onBackPressed() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (depth == 1) {
            recyclerView.adapter = mAdapter
            depth--
            when (currentList[0] == "창세기 ") {
                true -> currentTitle = "신약 성경"
                false -> currentTitle = "구약 성경"
            }
            toolbar.title = currentTitle
        } else if (depth == 2) {
            recyclerView.adapter = pageAdapter
            depth--
            toolbar.title = "${currentTitle.replace("""[0-9].*${'$'}""".toRegex(), "")}"
        } else {
            super.onBackPressed()
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

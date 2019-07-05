package com.example.bible

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MemoEdit : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var editText: EditText
    private val dbHelper = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memoedit_main)
        editText = findViewById(R.id.editText)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        val memo = intent.getStringExtra("MEMO")

        val first = memo?.get(0)


        if(first == '\n')
            editText.setText(memo.substring(1))
        else
            editText.setText(memo)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.memo_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.save_memo -> {
                val str = editText.text.toString()
                val id = intent?.getStringExtra("ID")
                if(id==null) {//새롭게 작성된 메모
                    dbHelper.addMemo(str)
                    if(intent.getStringExtra("MemoPage")==null){//라인페이지에서 넘어온 경우
                        val intent = Intent(this, MemoPage::class.java)
                        startActivity(intent)
                    }
                    else//메모페이지에서 넘어온 경우
                        setResult(RESULT_OK,intent)
                }
                else {//기존에 있는 메모
                    dbHelper.addMemo(id, str)
                    intent.putExtra("MEMO",str)
                    intent.putExtra("ID",id)
                    setResult(RESULT_OK,intent)
                }
                finish()
                true
            }
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
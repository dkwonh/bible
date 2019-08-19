package com.example.bible

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MemoEdit : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var editText: EditText
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
            R.id.close_app -> {
                dialog()
                true
            }
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
                Toast.makeText(this,"저장되었습니다",Toast.LENGTH_SHORT).show()
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
                intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
            R.id.nav_note -> {
                /*val intent = Intent(this,MemoPage::class.java)
                startActivity(intent)
                finish()*/
            }

            R.id.nav_daily -> {
                val intent = Intent(this, LinePage::class.java)
                intent.putExtra("PROVERBS","200")
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

    private fun dialog(){
        val exitDialog = ExitDialog(this)
        exitDialog.show()
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
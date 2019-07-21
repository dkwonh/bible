package com.example.bible

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MemoView : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var str: String? = null
    private var id: String? = null
    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memo_view_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        textView = findViewById(R.id.memoText)

        str = intent.getStringExtra("MEMO")

        textView.text = str

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        textView.movementMethod = ScrollingMovementMethod()

        navView.setNavigationItemSelectedListener(this)
    }

    private fun deleteButton(){
        val dbHelper = DBHelper(this)
        id = intent.getStringExtra("ID")
        dbHelper.deleteMemo(id.toString())
        Toast.makeText(this,"삭제되었습니다.",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun alertDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("메모 삭제")
        builder.setMessage("삭제하시겠습니까?")
        builder.setPositiveButton("예") { _, _ -> deleteButton() }
        builder.setNegativeButton("아니오") { _, _ -> }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    textView.text = data!!.getStringExtra("MEMO")
                    id = data.getStringExtra("ID")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.memo_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.update_memo -> {
                id = intent.getStringExtra("ID")
                val intent = Intent(this, MemoEdit::class.java)
                intent.putExtra("MEMO", textView.text.toString())
                intent.putExtra("ID", id)
                startActivityForResult(intent, 1)
                true
            }
            R.id.delete_memo -> {
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
                intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            R.id.nav_note -> {
                val intent = Intent(this,MemoPage::class.java)
                startActivity(intent)
                finish()
            }

            R.id.nav_daily -> {
                val intent = Intent(this, LinePage::class.java)
                intent.putExtra("PROVERBS","200")
                startActivity(intent)
                finish()
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

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        when (drawerLayout.isDrawerOpen((GravityCompat.START))) {
            true -> drawerLayout.closeDrawer(GravityCompat.START)
            false -> {
                super.onBackPressed()
            }
        }
    }
}
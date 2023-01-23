package com.example.guru16application

import android.content.Context
import android.content.res.AssetManager
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.guru16application.databinding.ActivityMainBinding
import com.example.guru16application.ui.settings.SettingsFragment
import com.example.guru16application.ui.ProductDBHelper
import com.example.guru16application.ui.shelter.ShelterFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    // ─────────────────────────────────── 변수 선언 ───────────────────────────────────
    // 내비게이션 프래그먼트
    private lateinit var binding: ActivityMainBinding

    // 데이터베이스
    lateinit var db: SQLiteDatabase
    var filePath: String = "/data/data/com.example.guru16application/databases/"

    // ─────────────────────────────────── 초 기 화 ───────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 내비게이션 프래그먼트와 툴바
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // 데이터베이스
        var check: File = File(filePath + "food.db")
        if (check.exists()) {

        } else {

            setDB(this)
            val mHelper: ProductDBHelper = ProductDBHelper(this, "food.db")
            db = mHelper.writableDatabase

        }

        // 내비게이션 프래그먼트 : 이동할 페이지 할당
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_clothing, R.id.navigation_food, R.id.navigation_shelter
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // ─────────────────────────────────── 툴바 함수 ───────────────────────────────────
    // 툴바 초기화
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 툴바 클릭 이벤트 정의
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.action_settings -> {
                val settingsFragment = SettingsFragment()
                show() // *사용자 정의 함수 : 세팅페이지 이동
                return true
            }
            else -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    // *사용자 정의 함수 : show()
    private fun show() {
        val fragManager = supportFragmentManager.beginTransaction()

        fragManager.replace(R.id.nav_host_fragment_activity_main, SettingsFragment()).commit()
    }

    // ─────────────────────────────────── 데이터베이스 ───────────────────────────────────
    private fun setDB(ctx: Context) {

        var folder: File = File(filePath)
        if (folder.exists()) {
        } else {
            folder.mkdirs();
        }
        var assetManager: AssetManager = ctx.resources.assets
        var outfile: File = File(filePath + "food.db")
        var IStr: InputStream? = null
        var fo: FileOutputStream? = null
        var filesize: Int = 0
        try {
            IStr = assetManager.open("food.db", AssetManager.ACCESS_BUFFER)
            filesize = IStr.available()
            if (outfile.length() <= 0) {
                val buffer = ByteArray(filesize)

                IStr.read(buffer)
                IStr.close()
                outfile.createNewFile()
                fo = FileOutputStream(outfile)
                fo.write(buffer)
                fo.close()
            } else {
            }
        } finally {
        }
    }

}
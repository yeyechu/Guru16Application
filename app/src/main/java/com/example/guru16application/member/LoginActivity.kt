package com.example.guru16application.member

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.guru16application.MainActivity
import com.example.guru16application.R
import com.example.guru16application.ui.ProductDBHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class LoginActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var  sqlDB : SQLiteDatabase

    lateinit var editID: EditText
    lateinit var editPW: EditText
    lateinit var btn_login: Button
    lateinit var btn_register: Button
    lateinit var autoLogin: CheckBox

    lateinit var str_name : String
    lateinit var str_id : String
    lateinit var str_pw : String

    var filePath: String = "/data/data/com.example.guru16application/databases/"

    lateinit var userid : datashare

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editID = findViewById(R.id.editID)
        editPW = findViewById(R.id.editPW)
        btn_login = findViewById(R.id.btn_login)
        btn_register = findViewById(R.id.btn_register)
        autoLogin = findViewById(R.id.autoLogin)

        // 첫 화면에서 db 읽어오기

        var check: File = File(filePath + "food.db")
        if (check.exists()) {

        } else {

            setDB(this)
            val mHelper: ProductDBHelper = ProductDBHelper(this, "food.db")
            sqlDB = mHelper.writableDatabase

        }

        //탈퇴 여부 확인
        val test = intent.getStringExtra("del").toString()
        if (test == "yes"){
            clearUser2()
            check("no")
        }


        //자동 로그인 클릭 시 user 불러 오기
        loadUser2()

        //기존에 자동 로그인 체크되었는지 확인
        val ckbox : String? = loadcheck()

        if(ckbox == "yes"){
            autoLogin.isChecked = true
        }


        // ───────────────────────── 이벤트 정의 : 로그인 버튼 ─────────────────────────

        btn_login.setOnClickListener {
            //var intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            var inputID = editID.text.toString()
            var inputPW = editPW.text.toString()

            dbManager = DBManager(this, "memberDB", null, 1)
            sqlDB = dbManager.readableDatabase

            var cursor : Cursor
            cursor = sqlDB.rawQuery("SELECT * FROM memberTBL WHERE id = '$inputID';", null)

            // 칼럼 제목 : name / tel / id / pw
            if(cursor != null && cursor.moveToFirst()) {

                str_name = cursor.getString(cursor.getColumnIndexOrThrow("name")).toString()
                str_id = cursor.getString(cursor.getColumnIndexOrThrow("id")).toString()
                str_pw = cursor.getString(cursor.getColumnIndexOrThrow("pw")).toString()

                cursor.close()
                sqlDB.close()
                dbManager.close()

                if(str_id == inputID)
                {
                    if(str_pw != inputPW)
                    {
                        Toast.makeText(applicationContext, "비밀번호가 다릅니다", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        if(autoLogin.isChecked()){
                            saveUser2(inputID, inputPW)
                            check("yes")
                        }else{
                            clearUser2()
                            check("no")
                        }


                        Toast.makeText(applicationContext, "$str_name 님 환영합니다", Toast.LENGTH_SHORT).show()

                        var intent = Intent(this, MainActivity::class.java)
                        //intent.putExtra("memberName", str_name)
                        //intent.putExtra("memberID", str_id)
                        userid = datashare
                        userid.setValue(inputID)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            else
            {
                Toast.makeText(applicationContext, "등록된 아이디가 없습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // ───────────────────────── 이벤트 정의 : 가입 버튼 ─────────────────────────

        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    // ───────────────────────── 자동 로그인을 위한 메소드 ─────────────────────────

    /*private fun saveUser(id : String, pw : String) {
        var pref = this.getPreferences(0)
        var editor = pref.edit()

        editor.putString("KEY_ID", editID.text.toString()).apply()
        editor.putString("KEY_PW", editPW.text.toString()).apply()
    }

    private fun loadUser() {
        var pref = this.getPreferences(0)
        var id = pref.getString("KEY_ID", "")
        var pw = pref.getString("KEY_PW", "")

        if(id != "" && pw != "")
        {
            editID.setText(id.toString())
            editPW.setText(pw.toString())
        }
    }*/

    // ───────────────────────── 자동 로그인을 위한 메소드 ─────────────────────────

    private fun saveUser2(id : String, pw : String) {
        var pref = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
        var editor = pref.edit()

        editor.putString("KEY_ID", editID.text.toString()).apply()
        editor.putString("KEY_PW", editPW.text.toString()).apply()
        editor.commit()
    }

    private fun loadUser2() {
        var pref = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
        var id = pref.getString("KEY_ID", "")
        var pw = pref.getString("KEY_PW", "")

        if(id != "" && pw != "")
        {
            editID.setText(id.toString())
            editPW.setText(pw.toString())
        }
    }


    //로그아웃 구현 시
    fun clearUser2() {
        var pref = this.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
        var editor = pref.edit()

        editor.clear()
        editor.commit()
    }

    fun check(chk : String) {
        var pref = this.getSharedPreferences("autoLoginBtn", Activity.MODE_PRIVATE)
        var editor = pref.edit()

        editor.putString("check", chk).apply()
        editor.commit()
    }

    private fun loadcheck() : String? {
        var pref = this.getSharedPreferences("autoLoginBtn", Activity.MODE_PRIVATE)
        var result = pref.getString("check", "")

        return result


    }


    //기존 db 파일 읽어오기

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

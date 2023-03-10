package com.example.guru16application.ui.shelter

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.GridView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.guru16application.MainActivity
import com.example.guru16application.databinding.FragmentHomeBinding
import com.example.guru16application.databinding.FragmentShelterBinding
import com.example.guru16application.ui.ProductDBHelper
import com.example.guru16application.ui.clothing.GriViewAdapter
import com.example.guru16application.ui.clothing.ReViewItem
import com.example.guru16application.ui.food.ListViewAdapter
import com.example.guru16application.ui.food.ListViewItem

// 주 tab 메인

class ShelterFragment : Fragment() {

    private var _binding: FragmentShelterBinding? = null

    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    lateinit var dbManager: ProductDBHelper
    lateinit var sqlitedb: SQLiteDatabase

    var list = arrayListOf<ListViewItem_s>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ShelterViewModel::class.java)

        _binding = FragmentShelterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textShelter
        shelterViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/



        //db 관련 코드 : 데이터가 있는 db 접속하기
        dbManager = ProductDBHelper(mainActivity,"food.db")
        sqlitedb = dbManager.readableDatabase
        sqlitedb = dbManager.writableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM shelter ;", null)

        while(cursor.moveToNext()){
            var building = cursor.getString((cursor.getColumnIndexOrThrow("shelname"))).toString()
            var classroom = cursor.getString((cursor.getColumnIndexOrThrow("shelloc"))).toString()
            list.add(ListViewItem_s(building,classroom))

        }

        val listView1: ListView = binding.listView
        notificationsViewModel.list_n.observe(viewLifecycleOwner) {

            val modelist : ArrayList<ListViewItem_s> = list
            val Adapter_1 = ListViewAdapter_s(mainActivity, modelist)
            listView1.adapter = Adapter_1
        }
        sqlitedb.close()




        binding.shelterSearchBtn.setOnClickListener {

            list.clear()
            downKeyboard()
            //Toast.makeText(mainActivity,"됨", Toast.LENGTH_SHORT).show()

            var str_text: String = binding.shelterSearchEdt.text.toString()

            sqlitedb = dbManager.readableDatabase
            sqlitedb = dbManager.writableDatabase

            if (str_text != "") {
                cursor =
                    sqlitedb.rawQuery(
                        "SELECT * FROM shelter WHERE shelname LIKE '%" + str_text + "%' OR shelloc LIKE '%" + str_text + "%'",
                        null
                    )
            } else {
                cursor = sqlitedb.rawQuery("SELECT * FROM shelter;", null)
            }


            while (cursor.moveToNext()) {
                var building = cursor.getString((cursor.getColumnIndexOrThrow("shelname"))).toString()
                var classroom = cursor.getString((cursor.getColumnIndexOrThrow("shelloc"))).toString()
                list.add(ListViewItem_s(building,classroom))

            }

            val listView2: ListView = binding.listView
            notificationsViewModel.list_n.observe(viewLifecycleOwner) {

                val modelist: ArrayList<ListViewItem_s> = list
                val Adapter_1 = ListViewAdapter_s(mainActivity, modelist)
                listView2.adapter = Adapter_1
            }

            sqlitedb.close()

            val new: EditText = binding.shelterSearchEdt
            notificationsViewModel.text.observe(viewLifecycleOwner) {
                new.text = null
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun downKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}
package com.example.attendanceapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.entities.Admin
import com.example.attendanceapp.data.entities.User
import com.example.attendanceapp.ui.admin.AdminPanelActivity
import com.example.attendanceapp.ui.user.UserPanelActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var lgEmail : EditText
    private lateinit var lgPsswd : EditText
    private lateinit var loginbtn : Button
//    private lateinit var adminlgin :RadioButton
//    private lateinit var userlgin :RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val userloginselection = findViewById<RadioGroup>(R.id.userlginslection)

        db = AppDatabase.getDatabase(this)

        lgEmail = findViewById(R.id.lgEmail)
        lgPsswd = findViewById(R.id.lgPsswd)
        loginbtn = findViewById(R.id.loginbtn)

        loginbtn.setOnClickListener {
            val email = lgEmail.text.toString()
            val password = lgPsswd.text.toString()
//            val selectedOptionId = userloginselection.checkedRadioButtonId
            if (email.isNotEmpty() && password.isNotEmpty()){
                val selectedOptionId = userloginselection.checkedRadioButtonId
                if (selectedOptionId == -1){
                    Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()

                }else{
                    val isAdmin = selectedOptionId == R.id.adminlgin
                    Log.d("@@2", "onCreate: " + isAdmin)
                    loginuser(email, password, isAdmin)

                }

            }else{
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }


        }
        userloginselection.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.adminOption ->{
                    Toast.makeText(this, "Admin Option selected", Toast.LENGTH_SHORT).show()

                }
                R.id.userOption ->{
                    Toast.makeText(this, "User Option selected", Toast.LENGTH_SHORT).show()


                }


            }
        }

    }

    private fun loginuser(email: String, password:String, isAdmin:Boolean){
        CoroutineScope(Dispatchers.IO).launch {


            if (isAdmin){
                val user = db.AdminDao().getAdminByEmailAndPassword(email, password)
                runOnUiThread {
                    if (user !=null && user.email == email && user.password == password){
                        saveUserId(user.id)
                        saveUserRole(isAdmin)
                        Log.d("@@@", "loginuser: " + user.id)
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = if (isAdmin) {
                            Intent(this@LoginActivity, AdminPanelActivity::class.java)
                        } else {
                            Intent(this@LoginActivity, UserPanelActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else{
                val user = db.userDao().getUserByEmailOrUsernameAndPassword(email, password)
                runOnUiThread {
                    if (user !=null && user.email == email && user.password == password){
                        saveUserId(user.id)
                        saveUserRole(isAdmin)
                        Log.d("@@@", "loginuser: " + user.id)
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = if (isAdmin) {
                            Intent(this@LoginActivity, AdminPanelActivity::class.java)
                        } else {
                            Intent(this@LoginActivity, UserPanelActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
    }

    private fun saveUserId(userId: Int) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("user_id", userId)
            apply() // Save changes asynchronously
        }
    }
    private fun saveUserRole(isAdmin: Boolean) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_admin", isAdmin)
            apply() // Save changes asynchronously
        }
}}
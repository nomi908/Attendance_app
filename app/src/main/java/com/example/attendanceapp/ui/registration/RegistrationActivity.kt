package com.example.attendanceapp.ui.registration

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.attendanceapp.R
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.data.dao.UserDao
import com.example.attendanceapp.data.entities.Admin
import com.example.attendanceapp.data.entities.User
import com.example.attendanceapp.ui.admin.AdminPanelActivity
import com.example.attendanceapp.ui.login.LoginActivity
import com.example.attendanceapp.ui.user.UserPanelActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private var userId: Int? = null
    private lateinit var whologin : RadioGroup
    private lateinit var adminOption : RadioButton
    private lateinit var userOption : RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)
        val isAdmin = sharedPreferences.getBoolean("is_admin", false) // Retrieve user role
        if (userId != -1){
            val intent = if (isAdmin){
                Intent(this@RegistrationActivity, AdminPanelActivity::class.java)
            }else{
                Intent(this@RegistrationActivity, UserPanelActivity::class.java)
            }
            startActivity(intent)
            finish()
        }


//        if (userId != -1){
//            startActivity(Intent(this@RegistrationActivity, UserPanelActivity::class.java))
//            finish()
//        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        db = AppDatabase.getDatabase(this)


        val crtName = findViewById<EditText>(R.id.crtName)
        val crtEmail = findViewById<EditText>(R.id.crtEmail)
        val crtPsswd = findViewById<EditText>(R.id.crtPsswd)
        val signupbtn = findViewById<Button>(R.id.signupbtn)
        val logintxt = findViewById<TextView>(R.id.logintext)
        whologin = findViewById(R.id.whologin)
        adminOption = findViewById(R.id.adminOption)
        userOption  = findViewById(R.id.userOption)

        whologin.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.adminOption ->{
                    Toast.makeText(this, "Admin Option selected", Toast.LENGTH_SHORT).show()

                }
                R.id.userOption ->{
                    Toast.makeText(this, "User Option selected", Toast.LENGTH_SHORT).show()


                }


            }
        }

        signupbtn.setOnClickListener {
            val username = crtName.text.toString()
            val useremail = crtEmail.text.toString()
            val password = crtPsswd.text.toString()
            val selectedOption = whologin.checkedRadioButtonId

            if (username.isNotEmpty() && useremail.isNotEmpty() &&  password.isNotEmpty() ){
                if (selectedOption == -1){
                    Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()

                }else {

                    val isAdmin = selectedOption == R.id.adminOption

                    registerUser(username, useremail, password, isAdmin)
                }
            }else{
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        logintxt.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
        }


    }

    private fun registerUser(username: String, email : String, password: String, isAdmin:Boolean){

        CoroutineScope(Dispatchers.IO).launch {
            if (isAdmin){

                val admin = Admin(username = username, email=email, password = password, )
                db.AdminDao().insert(admin)

            }else {

                val user = User(username = username, email = email, password = password)
                db.userDao().insert(user)
            }
            runOnUiThread {

                Toast.makeText(this@RegistrationActivity, "User registered successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
    }
    }

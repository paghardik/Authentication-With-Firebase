package com.example.authenticationwithfierbase

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.authenticationwithfierbase.MainActivity.Companion.CLAZZ
import com.example.authenticationwithfierbase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Adapter
        val adapter = MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLAZZ as Array<Class<*>>)
        adapter.setDescriptionIds(DESCRIPTION_IDS)

        binding.listView.adapter = adapter
        binding.listView.onItemClickListener = this

        HashKey.printHashKey(applicationContext)

    }

    override fun onStart() {
        super.onStart()
    }

    companion object{
        val CLAZZ = arrayOf(
            EmailPasswordActivity::class.java,
            GoogleSignIn::class.java,
            PhoneAuthActivity::class.java,
                FacebookLoginActivity::class.java
        )

        val DESCRIPTION_IDS = arrayOf(
            "Use Email/Password to Authentication with Firebase",
            "Use Google to Authentication with Firebase",
            "Use Phone Number to Authentication with Firebase",
            "Use FacebookLogin to Authentication with Firebase",
        )
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val clicked = CLAZZ[position]
        startActivity(Intent(this, clicked))
    }

}



class MyArrayAdapter(context: Context, resource: Int, private val clazz: Array<Class<*>>) : ArrayAdapter<Class<*>>(context, resource, clazz){

    private var descriptionIds: Array<String>? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (convertView == null){
            val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(android.R.layout.simple_list_item_2, null)
        }

        view?.findViewById<TextView>(android.R.id.text1)?.text = CLAZZ[position].simpleName
        view?.findViewById<TextView>(android.R.id.text2)?.text = descriptionIds!![position]

        return  view!!
    }

    fun setDescriptionIds(descriptionIds: Array<String>) {
        this.descriptionIds = descriptionIds
    }

}
package com.praptechie.example.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExampleServerDrivenUIViewModel : ViewModel() {
    private val _serverDrivenUIDataJson = MutableStateFlow<String?>(null)
    val serverDrivenUIDataJson: StateFlow<String?> get() = _serverDrivenUIDataJson

    private val database = Firebase.database
    private val myRef = database.getReference()

    private fun getServerDrivenUIData(){

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("uiJson","data - ${snapshot.value}")
                _serverDrivenUIDataJson.value=(snapshot.value.toString())
            }
            override fun onCancelled(error: DatabaseError) {}

        }
        myRef.addValueEventListener(listener)

        //myRef.removeEventListener(listener)
    }

    init {
        viewModelScope.launch {
            getServerDrivenUIData()
        }
    }

}
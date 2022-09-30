package com.example.sbma_staticfiles

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyFileViewModel: ViewModel() {
    private val _value: MutableLiveData<List<MyFile>> = MutableLiveData()
    val value: LiveData<List<MyFile>> = _value

    fun addFile(file: MyFile){
        _value.postValue(_value.value?.plus(file))
    }

    fun clearFiles(){
        _value.postValue(listOf())
    }
    
}


@Composable
fun ShowFiles(myFileViewModel: MyFileViewModel) {
    val value by myFileViewModel.value.observeAsState()
    if(value != null){
        LazyColumn{
            items(value!!){ file ->
                var prefix = "  ".repeat(file.level)
                if(file.level > 0){
                    prefix += if(file.isDirectory) " " else "└"
                }

                var text = prefix + file.name
                if(text.length > 34){
                    text = text.substring(0, 34) + "..."
                }
                Text(text, fontFamily = FontFamily.Monospace)
            }
        }
    }else{
        Text("Select a folder to list files")
    }

}
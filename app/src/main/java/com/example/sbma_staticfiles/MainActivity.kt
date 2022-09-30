package com.example.sbma_staticfiles

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.documentfile.provider.DocumentFile
import com.example.sbma_staticfiles.ui.theme.SBMA_StaticFilesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private lateinit var uri: Uri
        fun isInitialized(): Boolean = ::uri.isInitialized
        private val myFileViewModel = MyFileViewModel()
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            it.data?.also { data ->
                uri = data.data ?: return@registerForActivityResult
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                myFileViewModel.clearFiles()
                GlobalScope.launch(Dispatchers.IO) {
                    listFiles(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*val testFile = resources.openRawResource(R.raw.my_raw_file)
            val scanner: Scanner = Scanner(testFile)
            while(scanner.hasNextLine()){
                Log.d("pengb", scanner.nextLine())
            }*/
            SBMA_StaticFilesTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            openDocumentTree()
                        }
                    ){
                        Text("Select folder")
                    }
                    ShowFiles(myFileViewModel)
                }
            }
        }
    }


    private fun openDocumentTree(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startForResult.launch(intent)
    }

    private suspend fun listFiles(directoryUri: Uri, level: Int = 0){
        val documentsTree = DocumentFile.fromTreeUri(application, directoryUri)
        val childDocuments = documentsTree?.listFiles()
        childDocuments?.forEach {
            if(it.isDirectory){
                myFileViewModel.addFile(MyFile(level, it.name.toString(), true))
                listFiles(it.uri, level+1)
            }else{
                myFileViewModel.addFile(MyFile(level, it.name.toString(), false))
            }
        }
    }
}
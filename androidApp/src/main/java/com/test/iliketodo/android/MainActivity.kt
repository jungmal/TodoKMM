package com.test.iliketodo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.test.TODOItem
import com.test.iliketodo.AppDataBase
import com.test.iliketodo.DriverFactory
import com.test.iliketodo.Greeting

class MainActivity : ComponentActivity() {

    private lateinit var appDataBase: AppDataBase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDataBase = AppDataBase(DriverFactory(this))
        setContent {
            MyApplicationTheme {
                val todoItemList by appDataBase.getAllItemFlow().collectAsState(initial = emptyList())
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ToDoView(
                        todoItemList,
                        addAction = { title, imageUrl ->
                            appDataBase.insertItem(title, imageUrl)
                        },
                        deleteAction = { id ->
                            appDataBase.deleteItem(id)
                        },
                        checkToggle = { id, checked ->
                            appDataBase.updateCheck(checked, id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ToDoView(
    itemList: List<TODOItem>,
    addAction: (String, String) -> Unit = { _, _ -> },
    deleteAction: (Long) -> Unit = {},
    checkToggle: (Long, Boolean) -> Unit = { _, _ -> }
) {
    var titleText by remember {
        mutableStateOf("")
    }
    var imageUrlText by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            TextField(
                value = titleText,
                onValueChange = { titleText = it },
                modifier = Modifier.weight(3f),
                label = { Text("enter TODO title") }
            )
            OutlinedButton(
                onClick = {
                    addAction(titleText, imageUrlText)
                    titleText = ""
                    imageUrlText = ""
                },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {
                Text(text = "Add")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            TextField(
                value = imageUrlText,
                onValueChange = { imageUrlText = it },
                modifier = Modifier.weight(3f),
                label = { Text("enter TODO imageUrl") },
            )
        }
        LazyColumn {
            itemsIndexed(itemList) { index, item ->
                ToDoRow(
                    item = item,
                    deleteAction = {
                        deleteAction(item.id)
                    },
                    checkToggle = { checked ->
                        checkToggle(item.id, checked)
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
fun ToDoRow(
    item: TODOItem,
    deleteAction: () -> Unit = {},
    checkToggle: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.title,
            modifier = Modifier.weight(2f)
        )
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = item.isFinish,
            onCheckedChange = {
                checkToggle(it)
            },
            modifier = Modifier.weight(1f)
        )
        OutlinedButton(
            onClick = { deleteAction() },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Delete")
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        ToDoView(emptyList())
    }
}

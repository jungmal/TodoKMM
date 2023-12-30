package com.test.iliketodo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.test.TODOItem
import com.test.iliketodo.TodoUseCase
import com.test.iliketodo.DriverFactory

class MainActivity : ComponentActivity() {

    private lateinit var todoUseCase: TodoUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoUseCase = TodoUseCase(DriverFactory(this))
        setContent {
            MyApplicationTheme {
                val todoItemList by todoUseCase.getAllItemFlow().collectAsState(initial = emptyList())
                val deletedCount by todoUseCase.getFinishedItemCountFlow().collectAsState(initial = 0L)
                val lastDeleted by todoUseCase.getLatestDeletedItemFlow().collectAsState(initial = null)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colors.background),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "완료 후 삭제된 TODO 개수: $deletedCount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = "가장 최근에 삭제된 TODO: ${lastDeleted?.title ?: "없음"}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    ToDoView(
                        todoItemList,
                        addAction = { title, imageUrl ->
                            todoUseCase.insertItem(title, imageUrl)
                        },
                        deleteAction = { todoItem ->
                            todoUseCase.deleteItem(todoItem.id, todoItem.title, todoItem.imageUrl, todoItem.isFinish, System.currentTimeMillis())
                        },
                        checkToggle = { id, checked ->
                            todoUseCase.updateCheck(checked, id)
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
    deleteAction: (TODOItem) -> Unit = {},
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
                        deleteAction(item)
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

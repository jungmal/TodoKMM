package com.test.iliketodo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.test.TODOItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                TodoScreen()
            }
        }
    }
}

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel()
) {

    val state = viewModel.todoState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "완료 후 삭제된 TODO 개수: ${state.deletedCount}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "가장 최근에 삭제된 TODO: ${state.lastDeletedItem?.title ?: "없음"}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
        TextField(
            value = state.titleText,
            onValueChange = { viewModel.updateTitleText(it) },
            label = { Text("enter TODO title") },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        )
        TextField(
            value = state.imageUrlText,
            onValueChange = { viewModel.updateImageUrlText(it) },
            label = { Text("enter TODO imageUrl") },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        )
        OutlinedButton(
            onClick = {
                viewModel.addItem()
                viewModel.updateTitleText("")
                viewModel.updateImageUrlText("")
            },
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(45.dp)
        ) {
            Text(text = "ADD")
        }
        LazyColumn(
            modifier = Modifier.padding(top = 5.dp)
        ) {
            itemsIndexed(state.itemList) { index, item ->
                ToDoRow(
                    item = item,
                    deleteAction = {
                        viewModel.deleteItem(item)
                    },
                    checkToggle = { checked ->
                        viewModel.updateItem(item.id, checked)
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
            .wrapContentHeight()
            .padding(10.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { checkToggle(!item.isFinish) }
            ),
        verticalAlignment = Alignment.CenterVertically,
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
            modifier = Modifier.weight(0.7f),
        )
        IconButton(
            onClick = { deleteAction() },
            modifier = Modifier.weight(0.7f)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        TodoScreen()
    }
}

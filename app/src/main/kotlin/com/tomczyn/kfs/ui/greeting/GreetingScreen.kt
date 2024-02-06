package com.tomczyn.kfs.ui.greeting

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomczyn.kfs.ui.common.AppScreen
import com.tomczyn.kfs.ui.common.theme.KFSPlaygroundTheme
import com.tomczyn.kfs.ui.greeting.BottomSheet.Bar
import com.tomczyn.kfs.ui.greeting.BottomSheet.Foo
import java.io.Serializable

sealed interface BottomSheet : Serializable {
    data object None : BottomSheet

    data object Foo : BottomSheet
    data object Bar : BottomSheet
}

@Composable
fun GreetingScreen() {
    BottomSheetLayout {
        AppScreen(screenPadding = 0.dp) {
            Box {
                Greeting()
            }
        }
    }
}

@Composable
fun AppScope.Greeting() {
    Box {
        Column(modifier = Modifier.fillMaxSize()) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { showBottomSheet(Foo) },
            ) {
                Text(text = "Show bottom bar")
            }
        }
        registerBottomSheet(Foo) {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { showBottomSheet(Bar) }) {
                    Text(
                        text = "Foo",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        registerBottomSheet(Bar) {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { dismissBottomSheet() }) {
                    Text(
                        text = "Bar",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KFSPlaygroundTheme { GreetingScreen() }
}

@Composable
fun CustomBottomSheet(
    scope: AppScope,
    type: BottomSheet,
    content: @Composable (onDismiss: () -> Unit) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    if (scope.bottomSheet == type) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { scope.dismissBottomSheet() }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .align(Alignment.BottomCenter)
                    .background(
                        colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                content { scope.dismissBottomSheet() }
            }
        }
    }
}

@Composable
fun BottomSheetLayout(
    content: @Composable AppScope.() -> Unit,
) {
    var currentBottomSheet by rememberSaveable { mutableStateOf<BottomSheet>(BottomSheet.None) }
    val empty: @Composable () -> Unit = {}
    var sheets: Map<BottomSheet, @Composable () -> Unit> by remember {
        mutableStateOf(mapOf(BottomSheet.None to empty))
    }
    val appScope = remember {
        object : AppScope {
            override val bottomSheet: BottomSheet
                get() = currentBottomSheet

            override fun showBottomSheet(sheet: BottomSheet) {
                currentBottomSheet = sheet
            }

            @Composable
            override fun registerBottomSheet(sheet: BottomSheet, content: @Composable () -> Unit) {
                LaunchedEffect(Unit) {
                    sheets = sheets.toMutableMap().apply { put(sheet, content) }
                }
            }

            override fun dismissBottomSheet() {
                currentBottomSheet = BottomSheet.None
            }
        }
    }
    Box {
        appScope.content()
        if (currentBottomSheet != BottomSheet.None) {
            CustomBottomSheet(appScope, currentBottomSheet) {
                sheets[currentBottomSheet]?.invoke()
            }
        }
    }
}

interface AppScope {
    val bottomSheet: BottomSheet

    fun showBottomSheet(sheet: BottomSheet)

    @SuppressLint("ComposableNaming")
    @Composable
    fun registerBottomSheet(sheet: BottomSheet, content: @Composable () -> Unit)

    fun dismissBottomSheet()
}
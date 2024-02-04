package com.tomczyn.kfs.ui.greeting

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
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

//val LocalBottomSheetInvoker =
//    staticCompositionLocalOf<(BottomSheet) -> Unit> { error("No BottomSheetInvoker provided") }
val LocalBottomSheet =
    staticCompositionLocalOf<LocalBottomSheetScope> { error("No BottomSheet provided") }
//val LocalBottomSheetDismiss =
//    staticCompositionLocalOf<() -> Unit> { error("No onDismiss provided") }

interface LocalBottomSheetScope {
    val type: BottomSheet
    fun show(bottomSheet: BottomSheet)

    @Composable
    fun register(type: BottomSheet, content: @Composable () -> Unit)
    fun dismiss()
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
fun Greeting() {
    val bottomSheet = LocalBottomSheet.current
    Box {
        Column(modifier = Modifier.fillMaxSize()) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { bottomSheet.show(Foo) },
            ) {
                Text(text = "Show bottom bar")
            }
        }
        bottomSheet.register(Foo) {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { bottomSheet.show(Bar) }) {
                    Text(
                        text = "Foo",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        bottomSheet.register(Bar) {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { bottomSheet.dismiss() }) {
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
    type: BottomSheet,
    content: @Composable (onDismiss: () -> Unit) -> Unit,
) {
    val bottomSheet = LocalBottomSheet.current
    val colorScheme = MaterialTheme.colorScheme
    if (bottomSheet.type == type) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { bottomSheet.dismiss() }
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
                content { bottomSheet.dismiss() }
            }
        }
    }
}

@Composable
fun BottomSheetLayout(
    content: @Composable BottomSheetLayoutContext.() -> Unit,
) {
    var currentBottomSheet by rememberSaveable { mutableStateOf<BottomSheet>(BottomSheet.None) }
    val empty: @Composable () -> Unit = {}
    var sheets: Map<BottomSheet, @Composable () -> Unit> by remember {
        mutableStateOf(mapOf(BottomSheet.None to empty))
    }
    val bottomSheetState = remember {
        object : LocalBottomSheetScope {
            override val type: BottomSheet
                get() = currentBottomSheet

            override fun show(bottomSheet: BottomSheet) {
                currentBottomSheet = bottomSheet
            }

            @Composable
            override fun register(type: BottomSheet, content: @Composable () -> Unit) {
                LaunchedEffect(Unit) {
                    sheets = sheets.toMutableMap().apply { put(type, content) }
                }
            }

            override fun dismiss() {
                currentBottomSheet = BottomSheet.None
            }
        }
    }
    val context by remember { derivedStateOf { BottomSheetLayoutContext(bottomSheetState.type) } }
    BottomSheetProvider(bottomSheetState = bottomSheetState) {
        Box {
            context.content()
            if (currentBottomSheet != BottomSheet.None) {
                CustomBottomSheet(type = currentBottomSheet) {
                    sheets[currentBottomSheet]?.invoke()
                }
            }
        }
    }
}

@Composable
fun BottomSheetProvider(
    bottomSheetState: LocalBottomSheetScope,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalBottomSheet provides bottomSheetState) {
        content()
    }
}

data class BottomSheetLayoutContext(
    val bottomSheet: BottomSheet,
)
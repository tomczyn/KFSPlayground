package com.tomczyn.kfs.ui.greeting

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tomczyn.kfs.ui.common.AppScreen
import com.tomczyn.kfs.ui.common.theme.KFSPlaygroundTheme

@Composable
fun GreetingScreen() {
    AppScreen {
        Greeting("Android")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KFSPlaygroundTheme { GreetingScreen() }
}

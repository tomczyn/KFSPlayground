package com.tomczyn.kfs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.tomczyn.kfs.ui.common.KFSPlaygroundNavHost
import com.tomczyn.kfs.ui.common.theme.KFSPlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { KFSPlaygroundTheme { KFSPlaygroundNavHost() } }
    }
}

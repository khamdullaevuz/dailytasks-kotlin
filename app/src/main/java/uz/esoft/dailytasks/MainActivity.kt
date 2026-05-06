package uz.esoft.dailytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uz.esoft.dailytasks.ui.DailyTasksApp
import uz.esoft.dailytasks.ui.theme.DailyTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyTasksTheme {
                DailyTasksApp()
            }
        }
    }
}

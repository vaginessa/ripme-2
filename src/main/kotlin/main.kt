import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.rarchives.ripme.ripper.AbstractRipper

fun main() = singleWindowApplication(
    title = "RipMe",
    state = WindowState(width = 1280.dp, height = 768.dp),
) {
    MainView()
}

@Composable
fun MainView() {
    var screenState by remember { mutableStateOf<Screen>(Screen.LogScreen) }

    Column {
        TextInput()

        when (val screen = screenState) {
            is Screen.LogScreen ->
                LogScreen(onHistoryClick = { screenState = Screen.HistoryScreen })
            is Screen.HistoryScreen ->
                HistoryScreen(onHistoryClick = { screenState = Screen.LogScreen })
        }
    }
}

@Composable
private fun LogScreen(onHistoryClick: () -> Unit) {
    NavigationBar(onHistoryClick = onHistoryClick)
    LogView()
}

@Composable
private fun HistoryScreen(onHistoryClick: () -> Unit) {
    NavigationBar(onHistoryClick = onHistoryClick)
    HistoryView()
}

sealed class Screen {
    object LogScreen : Screen()
    object HistoryScreen : Screen()
}

private enum class NavType {
    LOG, HISTORY, QUEUE, SETTINGS
}

@Composable
private fun NavigationBar(onHistoryClick: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))
    val navItemState = remember { mutableStateOf(NavType.LOG) }

    BottomNavigation {
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource("black-comments-bubble-svgrepo-com.svg"),
                    contentDescription = "Log",
                    modifier = Modifier.fillMaxSize()
                )
            },
            selected = navItemState.value == NavType.LOG,
            onClick = { navItemState.value = NavType.LOG },
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource("history-svgrepo-com.svg"),
                    contentDescription = "History",
                    modifier = Modifier.fillMaxSize()
                )
            },
            selected = navItemState.value == NavType.HISTORY,
            onClick = {
                onHistoryClick
                navItemState.value = NavType.HISTORY
            },
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource("list-with-bullets-svgrepo-com.svg"),
                    contentDescription = "Queue",
                    modifier = Modifier.fillMaxSize()
                )
            },
            selected = navItemState.value == NavType.QUEUE,
            onClick = { navItemState.value = NavType.QUEUE },
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource("gear-symbol-svgrepo-com.svg"),
                    contentDescription = "Settings",
                    modifier = Modifier.fillMaxSize()
                )
            },
            selected = navItemState.value == NavType.SETTINGS,
            onClick = { navItemState.value = NavType.SETTINGS },
        )
    }
}

@Composable
private fun LogView() {
    OutlinedTextField(
        value = "Log ...",
        modifier = Modifier.padding(8.dp).fillMaxSize(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        label = { Text(text = "Log:") },
        onValueChange = {
        }
    )
}

@Composable
private fun HistoryView() {
    OutlinedTextField(
        value = "History ...",
        modifier = Modifier.padding(8.dp).fillMaxSize(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        label = { Text(text = "History:") },
        onValueChange = {
        }
    )
}


@Composable
private fun TextInput() {
    var ripUrl by remember { mutableStateOf(TextFieldValue("")) }
    OutlinedTextField(
        value = ripUrl,
        trailingIcon = {
            IconButton(onClick = {
                Rip(ripUrl)
            }) {
                Icon(
                    painterResource("download-from-cloud-svgrepo-com.svg"),
                    contentDescription = "Download",
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        label = { Text(text = "URL:") },
        placeholder = { Text(text = "Download URL.") },
        onValueChange = {
            ripUrl = it
        }
    )
}

private fun Rip(url: TextFieldValue) {
    try {
        val ripper: AbstractRipper = AbstractRipper.getRipper(java.net.URL(url.text.toString()))
        ripper.setup()
        ripper.rip()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
package com.seniordialer.presentation.ui.screens.dialer

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seniordialer.domain.model.ContactPhotoColors
import com.seniordialer.domain.model.FavoriteContact
import com.seniordialer.presentation.viewmodel.SeniorDialerViewModel
import com.seniordialer.util.DialHelper

private val dialPad = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf("*", "0", "#")
)

@Composable
fun DialerHomeScreen(vm: SeniorDialerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val number by vm.dialedNumber.collectAsStateWithLifecycle()
    val favorites by vm.contacts.collectAsStateWithLifecycle()
    val topSix = favorites.take(6)

    val callLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) DialHelper.dial(context, number)
        else Toast.makeText(context, "Call permission required", Toast.LENGTH_SHORT).show()
    }

    fun dialPhone(phone: String) {
        vm.setDialedNumber(phone)
        if (!DialHelper.hasCallPermission(context)) {
            callLauncher.launch(Manifest.permission.CALL_PHONE)
        } else {
            DialHelper.dial(context, phone)
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            number.ifEmpty { "Enter number" },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )
        if (topSix.isNotEmpty()) {
            Text("Favorites", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(topSix, key = { it.id }) { contact ->
                    FavoriteTile(contact) { dialPhone(contact.phone) }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        dialPad.forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { key ->
                    DialKey(key) {
                        when (key) {
                            in "0123456789*#" -> vm.appendDigit(key[0])
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(onClick = { vm.clearNumber() }, modifier = Modifier.height(64.dp)) {
                Text("Clear", style = MaterialTheme.typography.titleMedium)
            }
            IconButton(onClick = { vm.backspace() }, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Default.Backspace, "Backspace", modifier = Modifier.size(36.dp))
            }
            Button(
                onClick = {
                    if (number.isBlank()) {
                        Toast.makeText(context, "Enter a number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!DialHelper.hasCallPermission(context)) callLauncher.launch(Manifest.permission.CALL_PHONE)
                    else DialHelper.dial(context, number)
                },
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Icon(Icons.Default.Call, "Call", modifier = Modifier.size(32.dp), tint = Color.White)
            }
        }
    }
}

@Composable
private fun DialKey(label: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.size(width = 96.dp, height = 72.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Text(label, fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FavoriteTile(contact: FavoriteContact, onClick: () -> Unit) {
    val color = Color(contact.photoColor)
    Card(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.85f))
    ) {
        Column(
            Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.size(48.dp).background(Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    contact.name.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                contact.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onAddEdit: (Long?) -> Unit,
    vm: SeniorDialerViewModel = hiltViewModel()
) {
    val contacts by vm.contacts.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopAppBar({ Text("Favorites") }) },
        floatingActionButton = {
            if (contacts.size < 6) {
                FloatingActionButton(onClick = { onAddEdit(null) }) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Up to 6 large contact tiles on the dialer home screen. Tap to call.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            items(contacts, key = { it.id }) { contact ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(56.dp).background(Color(contact.photoColor), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(contact.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(contact.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text(contact.phone, style = MaterialTheme.typography.bodyLarge)
                        }
                        IconButton(onClick = { vm.moveContactUp(contact.id) }) {
                            Icon(Icons.Default.ArrowUpward, "Move up")
                        }
                        IconButton(onClick = { vm.moveContactDown(contact.id) }) {
                            Icon(Icons.Default.ArrowDownward, "Move down")
                        }
                        IconButton(onClick = { onAddEdit(contact.id) }) { Icon(Icons.Default.Edit, "Edit") }
                        IconButton(onClick = { vm.deleteContact(contact.id) }) { Icon(Icons.Default.Delete, "Delete") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContactScreen(
    contactId: Long?,
    onBack: () -> Unit,
    vm: SeniorDialerViewModel = hiltViewModel()
) {
    val contacts by vm.contacts.collectAsStateWithLifecycle()
    val existing = remember(contactId, contacts) { contacts.find { it.id == contactId } }
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var phone by remember { mutableStateOf(existing?.phone ?: "") }
    var photoColor by remember { mutableLongStateOf(existing?.photoColor ?: vm.nextPhotoColor()) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (contactId == null) "Add Contact" else "Edit Contact") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Text("Tile color", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ContactPhotoColors.forEach { color ->
                    FilterChip(
                        selected = photoColor == color,
                        onClick = { photoColor = color },
                        label = { Box(Modifier.size(24.dp).background(Color(color), CircleShape)) }
                    )
                }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank()) {
                        error = "Name and phone required"
                        return@Button
                    }
                    vm.saveContact(
                        FavoriteContact(
                            id = existing?.id ?: 0,
                            name = name.trim(),
                            phone = DialHelper.normalizePhone(phone.trim()),
                            photoColor = photoColor,
                            order = existing?.order ?: contacts.size
                        )
                    ) { ok, msg ->
                        if (ok) onBack() else error = msg
                    }
                },
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) { Text("Save", style = MaterialTheme.typography.titleLarge) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerSettingsScreen(vm: SeniorDialerViewModel = hiltViewModel()) {
    val prefs by vm.preferences.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar({ Text("Settings") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("High contrast", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                Switch(checked = prefs.highContrastMode, onCheckedChange = { vm.updateHighContrast(it) })
            }
            Text(
                "Large buttons and 1.4× text for easier reading. Favorites appear on the dialer home screen.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

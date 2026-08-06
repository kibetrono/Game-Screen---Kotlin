package com.example.eduapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.eduapp.database.User
import com.example.eduapp.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class SortOption(val label: String) {
    NEWEST("Newest"),
    HIGHEST_SCORE("Highest Score")
}

//History screen: shows every past game result, most recent first.
//Reads from the shared AppViewModel/AppDatabase singleton (no more per-screen DB setup).
//Safety: both "clear all" and per-entry delete require confirmation; edit is a
//clearly separate action (fixing a mistyped name) from delete (removing a record).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val users by appViewModel.users.collectAsStateWithLifecycle(initialValue = emptyList())
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()) }

    var sortOption by remember { mutableStateOf(SortOption.NEWEST) }
    // users already comes out of the DB sorted newest-first (see AppDao.getAllUsers),
    // so "Newest" needs no extra work here - only re-sort when Highest Score is picked.
    val sortedUsers = when (sortOption) {
        SortOption.NEWEST -> users
        SortOption.HIGHEST_SCORE -> users.sortedByDescending { it.score }
    }

    var clearAllPending by remember { mutableStateOf(false) }
    var entryPendingDelete by remember { mutableStateOf<User?>(null) }
    var entryPendingEdit by remember { mutableStateOf<User?>(null) }
    var editText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedButton(onClick = { clearAllPending = true }, enabled = users.isNotEmpty()) {
                Text("Clear History")
            }

            if (users.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SortOption.entries.forEach { option ->
                        FilterChip(
                            selected = sortOption == option,
                            onClick = { sortOption = option },
                            label = { Text(option.label) }
                        )
                    }
                }
            }

            if (users.isEmpty()) {
                Text(
                    text = "No games played yet.",
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedUsers, key = { it.id }) { user ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.username,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("Level ${user.level} · Score ${user.score} · ${user.duration}s")
                                    Text(
                                        text = dateFormat.format(Date(user.date)),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                IconButton(onClick = {
                                    entryPendingEdit = user
                                    editText = user.username
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit username")
                                }
                                IconButton(onClick = { entryPendingDelete = user }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete entry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Safety: confirm before clearing everything.
    if (clearAllPending) {
        AlertDialog(
            onDismissRequest = { clearAllPending = false },
            title = { Text("Clear all history?") },
            text = { Text("This will permanently delete every saved result. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    appViewModel.clearUsers()
                    clearAllPending = false
                }) { Text("Clear All") }
            },
            dismissButton = {
                TextButton(onClick = { clearAllPending = false }) { Text("Cancel") }
            }
        )
    }

    // Safety: confirm before deleting a single entry.
    entryPendingDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { entryPendingDelete = null },
            title = { Text("Delete this result?") },
            text = { Text("Delete ${user.username}'s Level ${user.level} result? This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    appViewModel.deleteResult(user)
                    entryPendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { entryPendingDelete = null } ) { Text("Cancel") }
            }
        )
    }

    // Edit: a separate, non-destructive action - just fixes the saved username.
    entryPendingEdit?.let { user ->
        AlertDialog(
            onDismissRequest = { entryPendingEdit = null },
            title = { Text("Edit username") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    singleLine = true,
                    label = { Text("Username") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        appViewModel.updateUsername(user, editText)
                        entryPendingEdit = null
                    },
                    enabled = editText.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { entryPendingEdit = null }) { Text("Cancel") }
            }
        )
    }
}
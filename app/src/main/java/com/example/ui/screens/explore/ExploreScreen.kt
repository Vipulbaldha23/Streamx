package com.example.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.LiveStream
import com.example.model.StreamCategory
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveBadge
import com.example.ui.components.StreamXTopBar
import com.example.ui.theme.*

@Composable
fun ExploreScreen(
    streams: List<LiveStream>,
    coinBalance: Int,
    unreadNotifications: Int,
    onStreamClick: (LiveStream) -> Unit,
    onWalletClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(StreamCategory.ALL) }
    var selectedLanguage by remember { mutableStateOf("All") }
    val languages = listOf("All", "English", "Hindi")

    val filteredStreams = remember(streams, searchQuery, selectedCategory, selectedLanguage) {
        streams.filter { stream ->
            val matchCategory = selectedCategory == StreamCategory.ALL || stream.category == selectedCategory
            val matchLang = selectedLanguage == "All" || stream.language.equals(selectedLanguage, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                    stream.title.contains(searchQuery, ignoreCase = true) ||
                    stream.streamerName.contains(searchQuery, ignoreCase = true) ||
                    stream.streamTags.any { it.contains(searchQuery, ignoreCase = true) }
            matchCategory && matchLang && matchQuery
        }
    }

    Scaffold(
        topBar = {
            StreamXTopBar(
                coinBalance = coinBalance,
                unreadNotifications = unreadNotifications,
                onWalletClick = onWalletClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        containerColor = VoidDark,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search creators, games, categories...", color = TextSecondaryDark, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null, tint = NeonCyan) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark.copy(alpha = 0.8f),
                        unfocusedContainerColor = SurfaceDark.copy(alpha = 0.6f),
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CardBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("explore_search_bar")
                )
            }

            // Categories horizontal bar
            item {
                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(StreamCategory.values()) { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                                .border(1.dp, if (isSelected) NeonCyan else CardBorderSubtle, RoundedCornerShape(18.dp))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${category.icon} ${category.displayName}",
                                color = if (isSelected) Color.White else TextSecondaryDark,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Language Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Language:", color = TextSecondaryDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    languages.forEach { lang ->
                        val isSelected = lang == selectedLanguage
                        Text(
                            text = lang,
                            color = if (isSelected) NeonCyan else TextMutedDark,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clickable { selectedLanguage = lang }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Grid Header
            item {
                Text(
                    text = "DISCOVER CHANNELS (${filteredStreams.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }

            // 2-Column Stream Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    filteredStreams.chunked(2).forEach { rowStreams ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowStreams.forEach { stream ->
                                GlassCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onStreamClick(stream) }
                                        .testTag("explore_stream_${stream.id}")
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(115.dp)
                                        ) {
                                            AsyncImage(
                                                model = stream.thumbnailUrl,
                                                contentDescription = stream.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(6.dp)
                                            ) {
                                                LiveBadge()
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(6.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color.Black.copy(alpha = 0.7f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "👥 ${stream.viewerCount}",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = stream.title,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = stream.streamerName,
                                                color = TextSecondaryDark,
                                                fontSize = 11.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowStreams.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

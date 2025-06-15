package com.example.fp_imk_mobile

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

data class MarkerInfo(val lat: Double, val lng: Double, val label: String, val address: String)
val locations = listOf(
    MarkerInfo(51.505, -0.09, "Marker 1", "Alamat 1"),
    MarkerInfo(51.515, -0.1, "Marker 2", "Alamat 2"),
    MarkerInfo(51.525, -0.08, "Marker 3", "Alamat 3"),
    MarkerInfo(51.555, -0.18, "Marker 4", "Alamat 4")
)

class LocationActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LocationScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen() {
    val context = LocalContext.current
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    var selectedMarker by remember { mutableStateOf<MarkerInfo?>(null) }
    val listState = rememberLazyListState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedInfo by remember { mutableStateOf<MarkerInfo?>(null) }


    LaunchedEffect(selectedMarker) {
        val index = locations.indexOf(selectedMarker)
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    val html = remember(locations) {
        val jsArray = locations.joinToString(",") {
            "[${it.lat}, ${it.lng}, '${it.label}']"
        }

        """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
          <style>
            html, body, #map { height: 100%; margin: 0; padding: 0; }
          </style>
        </head>
        <body>
          <div id="map"></div>
          <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
          <script>
            var map = L.map('map').setView([${locations.first().lat}, ${locations.first().lng}], 13);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
              attribution: '&copy; OpenStreetMap contributors'
            }).addTo(map);

            var markers = [];
            var locations = [$jsArray];
            locations.forEach(function(loc) {
              var marker = L.marker([loc[0], loc[1]]).addTo(map);
              marker.bindPopup(loc[2]);
              markers.push(marker);
            });

            function moveToLocation(lat, lng) {
              map.setView([lat, lng], 16);
            }
          </script>
        </body>
        </html>
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daftar Lokasi",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(
                factory = {
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

                        webViewRef.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Daftar Lokasi:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    itemsIndexed(locations) { index, loc ->
                        val isSelected = selectedMarker == loc
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFB3E5FC) else Color(0xFFE0E0E0),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxWidth()
                                .clickable {
                                    selectedMarker = loc
                                    selectedInfo = loc
                                    showDialog = true
                                    val js = "moveToLocation(${loc.lat}, ${loc.lng});"
                                    webViewRef.value?.evaluateJavascript(js, null)
                                }
                        ) {
                            Text(
                                text = loc.label,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                if (showDialog && selectedInfo != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text(text = selectedInfo!!.label, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        },
                        text = {
                            Text("Alamat: ${selectedInfo!!.address}", fontSize = 20.sp)
                        },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Tutup", fontSize = 16.sp)
                            }
                        }
                    )
                }

            }
        }
    }
}
package com.example.roadapp
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roadapp.model.Route

@Composable
fun RoutesList(data: List<Route>, onRouteSelected: (Route) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(data) { route ->
            Text(
                text = route.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRouteSelected(route) }
                    .padding(16.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RoutesListPreview() {
    val testData = listOf(
        Route("Testowa Trasa 1", "Opis 1"),
        Route("Testowa Trasa 2", "Opis 2")
    )
    RoutesList(testData, onRouteSelected = {} )
}
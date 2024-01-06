import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import model.BirdVO

@Composable
fun BirdAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = Color.Black,
        ),
        shapes = MaterialTheme.shapes.copy(
            small = AbsoluteCutCornerShape(0.dp),
            medium = AbsoluteCutCornerShape(0.dp),
            large = AbsoluteCutCornerShape(0.dp)
        ),
    ) {
        content()
    }
}

@Composable
fun App() {
    BirdAppTheme {
        val viewModel = getViewModel(Unit, viewModelFactory { BirdsViewModel() })
        val state by viewModel.state.collectAsState()


        LaunchedEffect(Unit) {
            viewModel.onAction(BirdsActions.LoadBirds)
        }

        BirdsScreen(state, onActions = viewModel::onAction)
    }
}

@Composable
fun BirdsScreen(state: BirdsState, onActions: (BirdsActions) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            state.categories.forEach { category ->
                Button(
                    onClick = { onActions(BirdsActions.SelectCategory(category)) },
                    modifier = Modifier.aspectRatio(1f).weight(1f).fillMaxSize(),
                    enabled = category != state.selectedCategory,
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                ) {
                    Text(text = category)
                }
            }
        }
        AnimatedVisibility(state.isLoading) {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                CircularProgressIndicator()
            }
        }
        AnimatedVisibility(visible = state.error != null) {
            Dialog(
                onDismissRequest = { },
            ) {
                Text("Error: ${state.error}")
                Button(onClick = { onActions(BirdsActions.LoadBirds) }) {
                    Text(text = "Reload")
                }
            }
        }
        AnimatedVisibility(visible = state.selectedImages.isNotEmpty() && state.isLoading.not()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.selectedImages) { bird ->
                    BirdItem(bird)
                }
            }
        }
    }

}

@Composable
fun BirdItem(bird: BirdVO) {
    KamelImage(
        resource = asyncPainterResource("https://sebi.io/demo-image-api/" + bird.path),
        contentDescription = bird.category,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        contentScale = ContentScale.Crop
    )
}

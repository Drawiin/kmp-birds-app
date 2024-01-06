import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.BirdVO

data class BirdsState(
    val birds: List<BirdVO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String? = null
) {
    val categories = birds.map { it.category }.toSet()
    val selectedImages =
        birds.filter { it.category == selectedCategory || selectedCategory == null }
}

sealed interface BirdsActions {
    data object LoadBirds : BirdsActions
    data class SelectCategory(val category: String) : BirdsActions
}

class BirdsViewModel : ViewModel() {
    private val _state = MutableStateFlow(BirdsState())
    val state = _state.asStateFlow()

    fun onAction(action: BirdsActions) {
        when (action) {
            is BirdsActions.SelectCategory -> {
                _state.update { it.copy(selectedCategory = action.category) }
            }

            BirdsActions.LoadBirds -> {
                loadBirds()
            }
        }
    }

    private fun loadBirds() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                _state.update { it.copy(birds = getBirds()) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    override fun onCleared() {
        httpClient.close()
        super.onCleared()
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private suspend fun getBirds(): List<BirdVO> {
        return httpClient.get("https://sebi.io/demo-image-api/pictures.json")
            .body<List<BirdVO>>()
    }

}
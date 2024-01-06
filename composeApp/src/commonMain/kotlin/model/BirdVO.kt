package model
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
@Immutable
data class BirdVO(
    @SerialName("author")
    val author: String,
    @SerialName("category")
    val category: String,
    @SerialName("path")
    val path: String
)
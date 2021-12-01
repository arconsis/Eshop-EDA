import java.util.*

data class Address(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val address: String,
    val houseNumber: String,
    val postalCode: String,
    val city: String,
    val phone: String,
)
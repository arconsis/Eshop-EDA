import java.util.*

data class Address(
    val id: UUID,
    val name: String,
    val address: String,
    val houseNumber: String,
    val countryCode: CountryCode,
    val postalCode: String,
    val city: String,
    val phone: String,
    val isBilling: Boolean = false,
)

enum class CountryCode {
    DE,
    GR,
    UK,
}
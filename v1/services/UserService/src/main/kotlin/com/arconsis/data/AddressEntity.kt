import com.arconsis.data.UserEntity
import java.util.*
import javax.persistence.*

@Entity(name = "addresses")
class AddressEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,
    @Column
    var name: String,
    @Column
    var address: String,
    @Column(name = "house_number")
    var houseNumber: String,
    @Column(name = "country_code")
    var countryCode: CountryCode,
    @Column(name = "postal_code")
    var postalCode: String,
    @Column
    var city: String,
    @Column
    var phone: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var userEntity: UserEntity,
)


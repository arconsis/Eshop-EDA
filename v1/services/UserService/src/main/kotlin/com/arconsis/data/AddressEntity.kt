import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.UserEntity
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.util.*
import javax.persistence.*

@NamedQueries(
    NamedQuery(
        name = "list_user_addresses",
        query = """ select a from addresses a
                    where a.userEntity.id = :user_id
                        """
    )
)
@Entity(name = "addresses")
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "country_code")
    @Type(type = "pgsql_enum")
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


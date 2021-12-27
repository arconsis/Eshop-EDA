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
    ),
    NamedQuery(
        name = "get_billing_address",
        query = """ select a from addresses a
                    where a.userEntity.id = :user_id and a.isBilling = true
                        """
    ),
    NamedQuery(
        name = "delete_billing_address",
        query = """ update addresses a 
                    set a.isBilling  = case a.isBilling
                    when true then false else false end
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

    @Column(name = "is_billing")
    var isBilling: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var userEntity: UserEntity,
)

fun setBillingAddress(addressEntity: AddressEntity): AddressEntity {
    addressEntity.isBilling = true
    return addressEntity
}





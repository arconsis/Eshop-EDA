import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.data.UserEntity
import com.arconsis.data.common.ADDRESS_ID
import com.arconsis.data.common.USER_ID
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.util.*
import javax.persistence.*

@NamedQueries(
    NamedQuery(
        name = AddressEntity.LIST_USER_ADDRESSES,
        query = """ select a from addresses a
                    where a.userEntity.id = :$USER_ID
                        """
    ),
    NamedQuery(
        name = AddressEntity.GET_BILLING_ADDRESS,
        query = """ select a from addresses a
                    where a.userEntity.id = :$USER_ID and a.isBilling = true
                        """
    ),
    NamedQuery(
        name = AddressEntity.DELETE_BILLING_ADDRESS,
        query = """ update addresses a 
                    set a.isBilling  = case a.isBilling
                    when true then false else false end
                    where a.userEntity.id = :$USER_ID
                        """
    ),
    NamedQuery(
        name = AddressEntity.GET_PREFERRED_SHIPPING_ADDRESSES,
        query = """ select a from addresses a
                    where a.userEntity.id = :$USER_ID and a.isPreferredShipping = true
                        """
    ),
    NamedQuery(
        name = AddressEntity.DELETE_PREFERRED_SHIPPING_ADDRESS,
        query = """ update addresses a 
                    set a.isPreferredShipping  = case a.isPreferredShipping
                    when true then false else false end
                    where a.userEntity.id = :$USER_ID and a.id = :$ADDRESS_ID
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

    @Column(name = "is_preferred_shipping")
    var isPreferredShipping: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var userEntity: UserEntity,
) {
    companion object {
        const val LIST_USER_ADDRESSES = "list_user_addresses"
        const val GET_BILLING_ADDRESS = "get_billing_address"
        const val DELETE_BILLING_ADDRESS = "delete_billing_address"
        const val GET_PREFERRED_SHIPPING_ADDRESSES = "get_preferred_shipping_address"
        const val DELETE_PREFERRED_SHIPPING_ADDRESS = "delete_preferred_shipping_address"
    }
}

fun AddressEntity.setAsBillingAddress() {
    this.isBilling = true
}

fun AddressEntity.setAsPreferredShippingAddress() {
    this.isPreferredShipping = true
}


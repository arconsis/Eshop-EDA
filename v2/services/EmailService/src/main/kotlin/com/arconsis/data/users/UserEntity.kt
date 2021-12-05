package com.arconsis.data.users

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@NamedQueries(
    NamedQuery(
        name = UserEntity.GET_USER_BY_USER_ID,
        query = """
            select us from users us
			where us.userId = :userId
        """
    ),
)
@Entity(name = "users")
@Table(name = "users")
class UserEntity(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "user_id")
    var userId: UUID,

    @Column(name = "first_name")
    var firstName: String,

    @Column(name = "last_name")
    var lastName: String,

    @Column
    var email: String,

    @Column
    var username: String,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
) {
    companion object {
        const val GET_USER_BY_USER_ID = "GET_USER_BY_USER_ID"
    }
}
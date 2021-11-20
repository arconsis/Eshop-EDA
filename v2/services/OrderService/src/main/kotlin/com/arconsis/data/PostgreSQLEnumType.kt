package com.arconsis.data

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.type.EnumType
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types

class PostgreSQLEnumType : EnumType<Enum<*>>() {
    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeSet(
        st: PreparedStatement,
        value: Any?,
        index: Int,
        session: SharedSessionContractImplementor,
    ) {
        st.setObject(
            index,
            if (value != null) (value as Enum<*>).name else null,
            Types.OTHER
        )
    }
}
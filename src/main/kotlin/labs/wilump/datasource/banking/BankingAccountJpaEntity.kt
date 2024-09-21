package labs.wilump.datasource.banking

import jakarta.persistence.*
import labs.wilump.datasource.common.persistence.BaseTimeEntity

@Entity
@Table(name = "account")
class BankingAccountJpaEntity(
    val accountNumber: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
): BaseTimeEntity()
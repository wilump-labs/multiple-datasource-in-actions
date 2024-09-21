package labs.wilump.datasource.membership

import jakarta.persistence.*
import labs.wilump.datasource.common.persistence.BaseTimeEntity

@Entity
@Table(name = "member")
class MemberJpaEntity(
    val name: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
): BaseTimeEntity()
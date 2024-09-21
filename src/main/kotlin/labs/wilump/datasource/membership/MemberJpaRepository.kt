package labs.wilump.datasource.membership

import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository: JpaRepository<MemberJpaEntity, Long> {
    fun findByName(name: String): MemberJpaEntity?
}
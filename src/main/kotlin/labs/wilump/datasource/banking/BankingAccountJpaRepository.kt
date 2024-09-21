package labs.wilump.datasource.banking

import org.springframework.data.jpa.repository.JpaRepository

interface BankingAccountJpaRepository: JpaRepository<BankingAccountJpaEntity, Long> {
    fun findByAccountNumber(accountNumber: String): BankingAccountJpaEntity?
}
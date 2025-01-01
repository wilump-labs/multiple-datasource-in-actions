package labs.wilump.datasource.common.config

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager.isCurrentTransactionReadOnly

class DynamicRoutingDataSource : AbstractRoutingDataSource() {

    override fun determineCurrentLookupKey(): Any {
        return if (isCurrentTransactionReadOnly()) {
            READER
        } else {
            WRITER
        }
    }

    companion object {
        const val WRITER = "writer"
        const val READER = "reader"
    }
}
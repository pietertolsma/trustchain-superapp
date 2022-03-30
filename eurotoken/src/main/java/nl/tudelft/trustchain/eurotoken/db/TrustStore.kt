package nl.tudelft.trustchain.eurotoken.db
import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import nl.tudelft.eurotoken.sqldelight.Database
import nl.tudelft.trustchain.common.contacts.ContactStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import nl.tudelft.ipv8.keyvault.PublicKey
import nl.tudelft.ipv8.keyvault.defaultCryptoProvider
import nl.tudelft.trustchain.eurotoken.entity.TrustScore
import java.sql.Blob
import java.util.*

class TrustStore (context: Context) {
    private val driver = AndroidSqliteDriver(Database.Schema, context, "eurotoken.db")
    private val database = Database(driver)
    val contactsStore = ContactStore.getInstance(context)

    private val messageMapper = {
            public_key : ByteArray,
            score : Long
        ->
        TrustScore(
            defaultCryptoProvider.keyFromPublicBin(public_key),
            score.toInt()
        )
    }

    fun getAllScores() : Flow<List<TrustScore>> {
        return database.dbTrustScoreQueries.getAll(messageMapper)
            .asFlow().mapToList()
    }

    fun getScore(publicKey: PublicKey) : Long? {
        return database.dbTrustScoreQueries.getScore(publicKey.keyToBin()).executeAsOneOrNull()
    }

    fun incrementTrust(publicKey: PublicKey) {
        val score : Long? = getScore(publicKey)
        return if (score != null) {
            database.dbTrustScoreQueries.incrementScore(publicKey.keyToBin())
        } else {
            database.dbTrustScoreQueries.addScore(publicKey.keyToBin(), 0)
        }
    }

    fun createContactStateTable() {
        database.dbTrustScoreQueries.createContactStateTable()
    }

    companion object {
        private lateinit var instance: TrustStore
        fun getInstance(context: Context): TrustStore {
            if (!::instance.isInitialized) {
                instance = TrustStore(context)
            }
            return instance
        }
    }
}

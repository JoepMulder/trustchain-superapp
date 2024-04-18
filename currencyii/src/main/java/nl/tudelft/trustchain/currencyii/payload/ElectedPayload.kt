package nl.tudelft.trustchain.currencyii.payload

import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.messaging.*
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class ElectedPayload(
    val DAOid: ByteArray,
) : Serializable {
    override fun serialize(): ByteArray {
        return serializeUShort(DAOid.size) + DAOid
    }

    companion object Deserializer: Deserializable<ElectedPayload> {
        override fun deserialize(
            buffer: ByteArray,
            offset: Int,
        ): Pair<ElectedPayload, Int> {
            var localOffset = 0
            val payloadSize = deserializeUShort(buffer, offset)
            localOffset += SERIALIZED_USHORT_SIZE
            val publicKey = buffer.copyOfRange(offset + localOffset, offset + localOffset + payloadSize)
            localOffset += payloadSize
            return Pair(ElectedPayload(publicKey), localOffset)
        }
        fun deserializeBytes(buffer: ByteArray, offset: Int): Integer {
            val pair = deserialize(buffer, offset)
            val daoIdBytes = pair.first.DAOid

            val daoId = ByteArrayInputStream(daoIdBytes).use { bis ->
                ObjectInputStream(bis).use { ois ->
                    ois.readObject() as Integer
                }
            }
            return daoId
        }
    }
}

package nl.tudelft.trustchain.currencyii.payload

import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.messaging.*
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class ElectedPayload(
    val DAOid: ByteArray,
    val leader: ByteArray,
) : Serializable {
    override fun serialize(): ByteArray {
        val daoIdSizeBytes = serializeUShort(DAOid.size)
        val leaderSizeBytes = serializeUShort(leader.size)
        return daoIdSizeBytes + DAOid + leaderSizeBytes + leader
//        return serializeUShort(DAOid.size) + DAOid
    }

    companion object Deserializer: Deserializable<ElectedPayload> {
        override fun deserialize(
            buffer: ByteArray,
            offset: Int,
        ): Pair<ElectedPayload, Int> {
            var localOffset = 0
            val daoIdSize = deserializeUShort(buffer, offset + localOffset)
            localOffset += SERIALIZED_USHORT_SIZE
            val daoId = buffer.copyOfRange(offset + localOffset, offset + localOffset + daoIdSize)
            localOffset += daoIdSize
            val leaderSize = deserializeUShort(buffer, offset + localOffset)
            localOffset += SERIALIZED_USHORT_SIZE
            val leader = buffer.copyOfRange(offset + localOffset, offset + localOffset + leaderSize)
            localOffset += leaderSize
            return Pair(ElectedPayload(daoId, leader), localOffset)
//            var localOffset = 0
//            val payloadSize = deserializeUShort(buffer, offset)
//            localOffset += SERIALIZED_USHORT_SIZE
//            val publicKey = buffer.copyOfRange(offset + localOffset, offset + localOffset + payloadSize)
//            localOffset += payloadSize
//            return Pair(ElectedPayload(publicKey), localOffset)
        }
        fun deserializeBytes(buffer: ByteArray, offset: Int): Pair<Integer, Peer> {
            val pair = deserialize(buffer, offset)
            val daoIdBytes = pair.first.DAOid
            val leaderBytes = pair.first.leader

            val daoId = ByteArrayInputStream(daoIdBytes).use { bis ->
                ObjectInputStream(bis).use { ois ->
                    ois.readObject() as Integer
                }
            }
            val leader = ByteArrayInputStream(leaderBytes).use { bis ->
                ObjectInputStream(bis).use { ois ->
                    ois.readObject() as Peer
                }
            }
            return Pair(daoId, leader)
        }

    }
}

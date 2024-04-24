package nl.tudelft.trustchain.currencyii.payload

import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.messaging.*
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class AlivePayload(
    val DAOid: ByteArray,
) : Serializable {
    override fun serialize(): ByteArray {
        return serializeUShort(DAOid.size) + DAOid
    }

    companion object Deserializer: Deserializable<AlivePayload> {
        override fun deserialize(
            buffer: ByteArray,
            offset: Int,
        ): Pair<AlivePayload, Int> {
            var localOffset = 0
            val payloadSize = deserializeUShort(buffer, offset)
            localOffset += SERIALIZED_USHORT_SIZE
            val publicKey = buffer.copyOfRange(offset + localOffset, offset + localOffset + payloadSize)
            localOffset += payloadSize
            return Pair(AlivePayload(publicKey), localOffset)
        }
    }
}

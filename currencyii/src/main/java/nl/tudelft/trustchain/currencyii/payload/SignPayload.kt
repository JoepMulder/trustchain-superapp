package nl.tudelft.trustchain.currencyii.payload
import nl.tudelft.ipv8.messaging.*
class SignPayload(
    val DAOid: ByteArray,
    val mostRecentSWBlock: ByteArray,
    val proposeBlockData: ByteArray,
    val signatures: ByteArray,
    val context: ByteArray
) : Serializable {
    override fun serialize(): ByteArray {
        val daoIdSizeBytes = serializeUShort(DAOid.size)
        val mostRecentSWBlockSizeBytes = serializeUShort(mostRecentSWBlock.size)
        val proposeBlockDataSizeBytes = serializeUShort(proposeBlockData.size)
        val signaturesSizeBytes = serializeUShort(signatures.size)
        val contextSizeBytes = serializeUShort(context.size)
        return daoIdSizeBytes + DAOid +
            mostRecentSWBlockSizeBytes + mostRecentSWBlock +
            proposeBlockDataSizeBytes + proposeBlockData +
            signaturesSizeBytes + signatures +
            contextSizeBytes + context
    }

    companion object Deserializer : Deserializable<SignPayload> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<SignPayload, Int> {
            var localOffset = 0
            val daoIdSize = deserializeUShort(buffer, offset + localOffset)
            localOffset += SERIALIZED_USHORT_SIZE
            val daoId = buffer.copyOfRange(offset + localOffset, offset + localOffset + daoIdSize)
            localOffset += daoIdSize

            val mostRecentSWBlockSize = deserializeUShort(buffer, offset + localOffset)
            localOffset += SERIALIZED_USHORT_SIZE
            val mostRecentSWBlock = buffer.copyOfRange(offset + localOffset, offset + localOffset + mostRecentSWBlockSize)
            localOffset += mostRecentSWBlockSize

            val proposeBlockDataSize = deserializeUShort(buffer, offset + localOffset)
            localOffset += SERIALIZED_USHORT_SIZE
            val proposeBlockData = buffer.copyOfRange(offset + localOffset, offset + localOffset + proposeBlockDataSize)
            localOffset += proposeBlockDataSize

            val signaturesSize = deserializeUShort(buffer, offset + localOffset)
            localOffset += SERIALIZED_USHORT_SIZE
            val signatures = buffer.copyOfRange(offset + localOffset, offset + localOffset + signaturesSize)
            localOffset += signaturesSize

            val contextSize = deserializeUShort(buffer, offset + localOffset)
            localOffset += SERIALIZED_USHORT_SIZE
            val context = buffer.copyOfRange(offset + localOffset, offset + localOffset + contextSize)
            localOffset += contextSize

            return Pair(SignPayload(daoId, mostRecentSWBlock, proposeBlockData, signatures, context), localOffset)
        }
    }
}


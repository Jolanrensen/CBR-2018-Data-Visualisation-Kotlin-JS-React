package data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Double::class)
object PercentageSerializer : KSerializer<Double?> {
    override val descriptor: SerialDescriptor =
        StringDescriptor.withName("PercentageSerializer")

    override fun serialize(encoder: Encoder, obj: Double?) {
        encoder.encodeString(
           obj?.let { "${(it * 100).toInt()}%" } ?: "-"
        )
    }

    /** Can't decode serialized Percentage! Serializer only used for pretty printing Double */
    override fun deserialize(decoder: Decoder) = 0.0
}
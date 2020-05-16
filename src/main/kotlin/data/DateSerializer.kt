package data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import kotlin.js.Date

@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        StringDescriptor.withName("DateSerializer")

    override fun serialize(encoder: Encoder, obj: Date) {
        encoder.encodeString(
            if (obj.getFullYear() == 10000) "-"
            else "${obj.getDate()}-${obj.getMonth()}-${obj.getFullYear()}"
        )
    }

    /** Can't decode serialized Date! Serializer only used for pretty printing Date */
    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeString())
    }
}
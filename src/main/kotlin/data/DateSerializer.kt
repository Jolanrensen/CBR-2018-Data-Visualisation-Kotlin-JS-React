package data

import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import kotlin.js.Date

@Serializer(forClass = DateSerializer::class)
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        StringDescriptor.withName("DateSerializer")

    override fun serialize(output: Encoder, obj: Date) {
        output.encodeString(obj.toDateString())
    }

    override fun deserialize(input: Decoder): Date {
        return Date(input.decodeString())
    }
}
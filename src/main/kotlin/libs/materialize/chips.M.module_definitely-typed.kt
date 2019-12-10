@file:JsQualifier("M")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package M

import Cash
import JQuery
import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external open class Chips : Component<ChipsOptions> {
    open var chipsData: Array<ChipData>
    open var hasAutocomplete: Boolean
    open var autocomplete: Autocomplete
    open fun addChip(chip: ChipData)
    open fun deleteChip(n: Number? = definedExternally)
    open fun selectChip(n: Number)

    companion object {
        fun getInstance(elem: Element): Chips
        fun init(els: Element, options: ChipsOptionsPartial? = definedExternally): Chips
        fun init(els: NodeList, options: ChipsOptionsPartial? = definedExternally): Array<Chips>
        fun init(els: JQuery, options: ChipsOptionsPartial? = definedExternally): Array<Chips>
        fun init(els: Cash, options: ChipsOptionsPartial? = definedExternally): Array<Chips>
    }
}

external interface ChipData {
    var tag: String
    var img: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ChipsOptions {
    var data: Array<ChipData>
    var placeholder: String
    var secondaryPlaceholder: String
    var autocompleteOptions: Any
    var limit: Number
    var onChipAdd: (`this`: Chips, element: Element, chip: Element) -> Unit
    var onChipSelect: (`this`: Chips, element: Element, chip: Element) -> Unit
    var onChipDelete: (`this`: Chips, element: Element, chip: Element) -> Unit
}

external interface ChipsOptionsPartial {
    var data: Array<ChipData>?
        get() = definedExternally
        set(value) = definedExternally
    var placeholder: String?
        get() = definedExternally
        set(value) = definedExternally
    var secondaryPlaceholder: String?
        get() = definedExternally
        set(value) = definedExternally
    var autocompleteOptions: Partial<AutocompleteOptions>?
        get() = definedExternally
        set(value) = definedExternally
    var limit: Number?
        get() = definedExternally
        set(value) = definedExternally
    var onChipAdd: ((`this`: Chips, element: Element, chip: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onChipSelect: ((`this`: Chips, element: Element, chip: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onChipDelete: ((`this`: Chips, element: Element, chip: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
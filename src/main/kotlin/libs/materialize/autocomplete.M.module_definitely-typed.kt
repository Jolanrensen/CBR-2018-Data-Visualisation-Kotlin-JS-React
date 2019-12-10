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

external interface Partial<T>

external open class Autocomplete : Component<AutocompleteOptions> {
    open fun selectOption(el: Element)
    open fun updateData(data: AutocompleteData)
    open var isOpen: Boolean
    open var count: Number
    open var activeIndex: Number

    companion object {
        fun getInstance(elem: Element): Autocomplete
        fun init(els: Element, options: AutocompleteOptionsPartial? = definedExternally): Autocomplete
        fun init(els: NodeList, options: AutocompleteOptionsPartial? = definedExternally): Array<Autocomplete>
        fun init(els: JQuery, options: AutocompleteOptionsPartial? = definedExternally): Array<Autocomplete>
        fun init(els: Cash, options: AutocompleteOptionsPartial? = definedExternally): Array<Autocomplete>
    }
}

external interface AutocompleteData {
    @nativeGetter
    operator fun get(key: String): String?
    @nativeSetter
    operator fun set(key: String, value: String?)
}

external interface AutocompleteOptions {
    var data: AutocompleteData
    var limit: Number
    var onAutocomplete: (`this`: Autocomplete, text: String) -> Unit
    var minLength: Number
    var sortFunction: (a: String, b: String, inputText: String) -> Number
}

external interface AutocompleteOptionsPartial {
    var data: AutocompleteData?
        get() = definedExternally
        set(value) = definedExternally
    var limit: Number?
        get() = definedExternally
        set(value) = definedExternally
    var onAutocomplete: ((`this`: Autocomplete, text: String) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var minLength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sortFunction: ((a: String, b: String, inputText: String) -> Number)?
        get() = definedExternally
        set(value) = definedExternally
}
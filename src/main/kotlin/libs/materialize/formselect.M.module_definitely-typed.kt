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

external open class FormSelect : Component<FormSelectOptions> {
    open var isMultiple: Boolean
    open var wrapper: Element
    open var dropdownOptions: HTMLUListElement
    open var input: HTMLInputElement
    open var dropdown: Dropdown
    open fun getSelectedValues(): Array<String>

    companion object {
        fun getInstance(elem: Element): FormSelect
        fun init(els: Element, options: FormSelectOptionsPartial? = definedExternally): FormSelect
        fun init(els: NodeList, options: FormSelectOptionsPartial? = definedExternally): Array<FormSelect>
        fun init(els: JQuery, options: FormSelectOptionsPartial? = definedExternally): Array<FormSelect>
        fun init(els: Cash, options: FormSelectOptionsPartial? = definedExternally): Array<FormSelect>
    }
}

external interface FormSelectOptions {
    var classes: String
    var dropdownOptions: Any
}

external interface FormSelectOptionsPartial {
    var classes: String?
        get() = definedExternally
        set(value) = definedExternally
    var dropdownOptions: Partial<DropdownOptions>?
        get() = definedExternally
        set(value) = definedExternally
}
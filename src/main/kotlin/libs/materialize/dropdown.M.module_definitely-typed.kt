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

external open class Dropdown : Component<DropdownOptions> {
    open var id: String
    open var dropdownEl: Element
    open var isOpen: Boolean
    open var isScrollable: Boolean
    open var focusedIndex: Number
    open fun open()
    open fun close()
    open fun recalculateDimensions()

    companion object {
        fun getInstance(elem: Element): Dropdown
        fun init(els: Element, options: DropdownOptionsPartial? = definedExternally): Dropdown
        fun init(els: NodeList, options: DropdownOptionsPartial? = definedExternally): Array<Dropdown>
        fun init(els: JQuery, options: DropdownOptionsPartial? = definedExternally): Array<Dropdown>
        fun init(els: Cash, options: DropdownOptionsPartial? = definedExternally): Array<Dropdown>
    }
}

external interface DropdownOptions {
    var alignment: dynamic /* 'left' | 'right' */
        get() = definedExternally
        set(value) = definedExternally
    var autoTrigger: Boolean
    var constrainWidth: Boolean
    var container: Element
    var coverTrigger: Boolean
    var closeOnClick: Boolean
    var hover: Boolean
    var inDuration: Number
    var outDuration: Number
    var onOpenStart: (`this`: Dropdown, el: Element) -> Unit
    var onOpenEnd: (`this`: Dropdown, el: Element) -> Unit
    var onCloseStart: (`this`: Dropdown, el: Element) -> Unit
    var onCloseEnd: (`this`: Dropdown, el: Element) -> Unit
}

external interface DropdownOptionsPartial {
    var alignment: dynamic /* 'left' | 'right' */
        get() = definedExternally
        set(value) = definedExternally
    var autoTrigger: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var constrainWidth: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var container: Element?
        get() = definedExternally
        set(value) = definedExternally
    var coverTrigger: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var closeOnClick: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var hover: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var inDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var outDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenStart: ((`this`: Dropdown, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenEnd: ((`this`: Dropdown, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseStart: ((`this`: Dropdown, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseEnd: ((`this`: Dropdown, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
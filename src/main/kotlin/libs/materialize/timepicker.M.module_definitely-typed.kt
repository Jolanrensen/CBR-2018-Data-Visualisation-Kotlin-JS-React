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

external open class Timepicker : Component<TimepickerOptions> {
    open var isOpen: Boolean
    open var time: String
    open fun open()
    open fun close()
    open fun showView(view: String /* "hours" */)
    open fun showView(view: String /* "minutes" */)

    companion object {
        fun getInstance(elem: Element): Timepicker
        fun init(els: Element, options: TimepickerOptionsPartial? = definedExternally): Timepicker
        fun init(els: NodeList, options: TimepickerOptionsPartial? = definedExternally): Array<Timepicker>
        fun init(els: JQuery, options: TimepickerOptionsPartial? = definedExternally): Array<Timepicker>
        fun init(els: Cash, options: TimepickerOptionsPartial? = definedExternally): Array<Timepicker>
    }
}

external interface TimepickerOptions {
    var duration: Number
    var container: String
    var showClearBtn: Boolean
    var defaultTime: String
    var fromNow: Number
    var i18n: Any
    var autoClose: Boolean
    var twelveHour: Boolean
    var vibrate: Boolean
    var onOpenStart: (`this`: Modal, el: Element) -> Unit
    var onOpenEnd: (`this`: Modal, el: Element) -> Unit
    var onCloseStart: (`this`: Modal, el: Element) -> Unit
    var onCloseEnd: (`this`: Modal, el: Element) -> Unit
    var onSelect: (`this`: Modal, hour: Number, minute: Number) -> Unit
}

external interface TimepickerOptionsPartial {
    var duration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var container: String?
        get() = definedExternally
        set(value) = definedExternally
    var showClearBtn: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var defaultTime: String?
        get() = definedExternally
        set(value) = definedExternally
    var fromNow: Number?
        get() = definedExternally
        set(value) = definedExternally
    var i18n: Partial<InternationalizationOptions>?
        get() = definedExternally
        set(value) = definedExternally
    var autoClose: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var twelveHour: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var vibrate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenStart: ((`this`: Modal, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenEnd: ((`this`: Modal, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseStart: ((`this`: Modal, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseEnd: ((`this`: Modal, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onSelect: ((`this`: Modal, hour: Number, minute: Number) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
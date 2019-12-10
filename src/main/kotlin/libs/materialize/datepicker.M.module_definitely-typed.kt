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

external open class Datepicker : Component<DatepickerOptions>, Openable {
    override var isOpen: Boolean
    open var date: Date
    open var doneBtn: HTMLButtonElement
    open var clearBtn: HTMLButtonElement
    override fun open()
    override fun close()
    override fun toString(): String
    open fun setDate(date: Date? = definedExternally, preventOnSelect: Boolean? = definedExternally)
    open fun setDate(date: String? = definedExternally, preventOnSelect: Boolean? = definedExternally)
    open fun gotoDate(date: Date)
    open fun setInputValue()
    open fun setDate()

    companion object {
        fun getInstance(elem: Element): Datepicker
        fun init(els: Element, options: DatepickerOptionsPartial? = definedExternally): Datepicker
        fun init(els: NodeList, options: DatepickerOptionsPartial? = definedExternally): Array<Datepicker>
        fun init(els: JQuery, options: DatepickerOptionsPartial? = definedExternally): Array<Datepicker>
        fun init(els: Cash, options: DatepickerOptionsPartial? = definedExternally): Array<Datepicker>
    }
}

external interface DatepickerOptions {
    var autoClose: Boolean
    var format: String
    var parse: (value: String, format: String) -> Date
    var defaultDate: Date
    var setDefaultDate: Boolean
    var disableWeekends: Boolean
    var disableDayFn: (day: Date) -> Boolean
    var firstDay: Number
    var minDate: Date
    var maxDate: Date
    var yearRange: dynamic /* Number | Array<Number> */
        get() = definedExternally
        set(value) = definedExternally
    var isRTL: Boolean
    var showMonthAfterYear: Boolean
    var showDaysInNextAndPreviousMonths: Boolean
    var container: Element
    var showClearBtn: Boolean
    var i18n: Any
    var events: Array<String>
    var onSelect: (`this`: Datepicker, selectedDate: Date) -> Unit
    var onOpen: (`this`: Datepicker) -> Unit
    var onClose: (`this`: Datepicker) -> Unit
    var onDraw: (`this`: Datepicker) -> Unit
}

external interface DatepickerOptionsPartial {
    var autoClose: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var format: String?
        get() = definedExternally
        set(value) = definedExternally
    var parse: ((value: String, format: String) -> Date)?
        get() = definedExternally
        set(value) = definedExternally
    var defaultDate: Date?
        get() = definedExternally
        set(value) = definedExternally
    var setDefaultDate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var disableWeekends: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var disableDayFn: ((day: Date) -> Boolean)?
        get() = definedExternally
        set(value) = definedExternally
    var firstDay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var minDate: Date?
        get() = definedExternally
        set(value) = definedExternally
    var maxDate: Date?
        get() = definedExternally
        set(value) = definedExternally
    var yearRange: dynamic /* Number | Array<Number> */
        get() = definedExternally
        set(value) = definedExternally
    var isRTL: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showMonthAfterYear: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showDaysInNextAndPreviousMonths: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var container: Element?
        get() = definedExternally
        set(value) = definedExternally
    var showClearBtn: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var i18n: Partial<InternationalizationOptions>?
        get() = definedExternally
        set(value) = definedExternally
    var events: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var onSelect: ((`this`: Datepicker, selectedDate: Date) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onOpen: ((`this`: Datepicker) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onClose: ((`this`: Datepicker) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onDraw: ((`this`: Datepicker) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
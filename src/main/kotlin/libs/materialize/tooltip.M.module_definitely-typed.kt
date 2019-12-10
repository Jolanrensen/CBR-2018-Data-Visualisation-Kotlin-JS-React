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

external open class Tooltip : Component<TooltipOptions>, Openable {
    override fun open()
    override fun close()
    override var isOpen: Boolean
    open var isHovered: Boolean

    companion object {
        fun getInstance(elem: Element): Tooltip
        fun init(els: Element, options: TooltipOptionsPartial? = definedExternally): Tooltip
        fun init(els: NodeList, options: TooltipOptionsPartial? = definedExternally): Array<Tooltip>
        fun init(els: JQuery, options: TooltipOptionsPartial? = definedExternally): Array<Tooltip>
        fun init(els: Cash, options: TooltipOptionsPartial? = definedExternally): Array<Tooltip>
    }
}

external interface TooltipOptions {
    var exitDelay: Number
    var enterDelay: Number
    var html: String
    var margin: Number
    var inDuration: Number
    var outDuration: Number
    var position: dynamic /* 'top' | 'right' | 'bottom' | 'left' */
        get() = definedExternally
        set(value) = definedExternally
    var transitionMovement: Number
}

external interface TooltipOptionsPartial {
    var exitDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enterDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var html: String?
        get() = definedExternally
        set(value) = definedExternally
    var margin: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var outDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var position: dynamic /* 'top' | 'right' | 'bottom' | 'left' */
        get() = definedExternally
        set(value) = definedExternally
    var transitionMovement: Number?
        get() = definedExternally
        set(value) = definedExternally
}
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

external open class Pushpin : Component<PushpinOptions> {
    open var originalOffset: Number

    companion object {
        fun getInstance(elem: Element): Pushpin
        fun init(els: Element, options: PushpinOptionsPartial? = definedExternally): Pushpin
        fun init(els: NodeList, options: PushpinOptionsPartial? = definedExternally): Array<Pushpin>
        fun init(els: JQuery, options: PushpinOptionsPartial? = definedExternally): Array<Pushpin>
        fun init(els: Cash, options: PushpinOptionsPartial? = definedExternally): Array<Pushpin>
    }
}

external interface PushpinOptions {
    var top: Number
    var bottom: Number
    var offset: Number
    var onPositionChange: (`this`: Pushpin, position: dynamic /* "pinned" | "pin-top" | "pin-bottom" */) -> Unit
}

external interface PushpinOptionsPartial {
    var top: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bottom: Number?
        get() = definedExternally
        set(value) = definedExternally
    var offset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var onPositionChange: ((`this`: Pushpin, position: dynamic /* "pinned" | "pin-top" | "pin-bottom" */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
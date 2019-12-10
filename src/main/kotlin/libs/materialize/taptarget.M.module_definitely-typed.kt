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

external open class TapTarget : Component<TapTargetOptions> {
    open var isOpen: Boolean
    open fun open()
    open fun close()

    companion object {
        fun getInstance(elem: Element): TapTarget
        fun init(els: Element, options: TapTargetOptionsPartial? = definedExternally): TapTarget
        fun init(els: NodeList, options: TapTargetOptionsPartial? = definedExternally): Array<TapTarget>
        fun init(els: JQuery, options: TapTargetOptionsPartial? = definedExternally): Array<TapTarget>
        fun init(els: Cash, options: TapTargetOptionsPartial? = definedExternally): Array<TapTarget>
    }
}

external interface TapTargetOptions {
    var onOpen: (`this`: TapTarget, origin: Element) -> Unit
    var onClose: (`this`: TapTarget, origin: Element) -> Unit
}

external interface TapTargetOptionsPartial {
    var onOpen: ((`this`: TapTarget, origin: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onClose: ((`this`: TapTarget, origin: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
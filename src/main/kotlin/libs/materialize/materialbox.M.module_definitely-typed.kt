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

external open class Materialbox : Component<MaterialboxOptions> {
    open var overlayActive: Boolean
    open var doneAnimating: Boolean
    open var caption: String
    open var originalWidth: Number
    open var originalHeight: Number
    open fun open()
    open fun close()

    companion object {
        fun getInstance(elem: Element): Materialbox
        fun init(els: Element, options: MaterialboxOptionsPartial? = definedExternally): Materialbox
        fun init(els: NodeList, options: MaterialboxOptionsPartial? = definedExternally): Array<Materialbox>
        fun init(els: JQuery, options: MaterialboxOptionsPartial? = definedExternally): Array<Materialbox>
        fun init(els: Cash, options: MaterialboxOptionsPartial? = definedExternally): Array<Materialbox>
    }
}

external interface MaterialboxOptions {
    var inDuration: Number
    var outDuration: Number
    var onOpenStart: (`this`: Materialbox, el: Element) -> Unit
    var onOpenEnd: (`this`: Materialbox, el: Element) -> Unit
    var onCloseStart: (`this`: Materialbox, el: Element) -> Unit
    var onCloseEnd: (`this`: Materialbox, el: Element) -> Unit
}

external interface MaterialboxOptionsPartial {
    var inDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var outDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenStart: ((`this`: Materialbox, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenEnd: ((`this`: Materialbox, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseStart: ((`this`: Materialbox, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseEnd: ((`this`: Materialbox, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
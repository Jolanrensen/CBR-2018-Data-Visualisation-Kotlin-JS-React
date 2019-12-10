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

external open class Collapsible : Component<CollapsibleOptions> {
    open fun open(n: Number)
    open fun close(n: Number)

    companion object {
        fun getInstance(elem: Element): Collapsible
        fun init(els: Element, options: CollapsibleOptionsPartial? = definedExternally): Collapsible
        fun init(els: NodeList, options: CollapsibleOptionsPartial? = definedExternally): Array<Collapsible>
        fun init(els: JQuery, options: CollapsibleOptionsPartial? = definedExternally): Array<Collapsible>
        fun init(els: Cash, options: CollapsibleOptionsPartial? = definedExternally): Array<Collapsible>
    }
}

external interface CollapsibleOptions {
    var accordion: Boolean
    var inDuration: Number
    var outDuration: Number
    var onOpenStart: (`this`: Collapsible, el: Element) -> Unit
    var onOpenEnd: (`this`: Collapsible, el: Element) -> Unit
    var onCloseStart: (`this`: Collapsible, el: Element) -> Unit
    var onCloseEnd: (`this`: Collapsible, el: Element) -> Unit
}

external interface CollapsibleOptionsPartial {
    var accordion: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var inDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var outDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenStart: ((`this`: Collapsible, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenEnd: ((`this`: Collapsible, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseStart: ((`this`: Collapsible, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseEnd: ((`this`: Collapsible, el: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
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

external open class Sidenav : Component<SidenavOptions>, Openable {
    override fun open()
    override fun close()
    override var isOpen: Boolean
    open var isFixed: Boolean
    open var isDragged: Boolean

    companion object {
        fun getInstance(elem: Element): Sidenav
        fun init(els: Element, options: SidenavOptionsPartial? = definedExternally): Sidenav
        fun init(els: NodeList, options: SidenavOptionsPartial? = definedExternally): Array<Sidenav>
        fun init(els: JQuery, options: SidenavOptionsPartial? = definedExternally): Array<Sidenav>
        fun init(els: Cash, options: SidenavOptionsPartial? = definedExternally): Array<Sidenav>
    }
}

external interface SidenavOptions {
    var edge: dynamic /* 'left' | 'right' */
        get() = definedExternally
        set(value) = definedExternally
    var draggable: Boolean
    var inDuration: Number
    var outDuration: Number
    var onOpenStart: (`this`: Sidenav, elem: Element) -> Unit
    var onOpenEnd: (`this`: Sidenav, elem: Element) -> Unit
    var onCloseStart: (`this`: Sidenav, elem: Element) -> Unit
    var onCloseEnd: (`this`: Sidenav, elem: Element) -> Unit
}

external interface SidenavOptionsPartial {
    var edge: dynamic /* 'left' | 'right' */
        get() = definedExternally
        set(value) = definedExternally
    var draggable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var inDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var outDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenStart: ((`this`: Sidenav, elem: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onOpenEnd: ((`this`: Sidenav, elem: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseStart: ((`this`: Sidenav, elem: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCloseEnd: ((`this`: Sidenav, elem: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}
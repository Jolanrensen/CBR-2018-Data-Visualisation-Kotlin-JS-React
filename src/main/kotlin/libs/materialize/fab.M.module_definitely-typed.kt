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

external open class FloatingActionButton : Component<FloatingActionButtonOptions>, Openable {
    override fun open()
    override fun close()
    override var isOpen: Boolean

    companion object {
        fun getInstance(elem: Element): FloatingActionButton
        fun init(els: Element, options: FloatingActionButtonOptionsPartial? = definedExternally): FloatingActionButton
        fun init(els: NodeList, options: FloatingActionButtonOptionsPartial? = definedExternally): Array<FloatingActionButton>
        fun init(els: JQuery, options: FloatingActionButtonOptionsPartial? = definedExternally): Array<FloatingActionButton>
        fun init(els: Cash, options: FloatingActionButtonOptionsPartial? = definedExternally): Array<FloatingActionButton>
    }
}

external interface FloatingActionButtonOptions {
    var direction: dynamic /* "top" | "right" | "buttom" | "left" */
        get() = definedExternally
        set(value) = definedExternally
    var hoverEnabled: Boolean
    var toolbarEnabled: Boolean
}

external interface FloatingActionButtonOptionsPartial {
    var direction: dynamic /* "top" | "right" | "buttom" | "left" */
        get() = definedExternally
        set(value) = definedExternally
    var hoverEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var toolbarEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}
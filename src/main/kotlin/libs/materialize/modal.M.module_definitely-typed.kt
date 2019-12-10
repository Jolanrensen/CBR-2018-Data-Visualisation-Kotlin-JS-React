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

external open class Modal : Component<ModalOptions>, Openable {
    override fun open()
    override fun close()
    override var isOpen: Boolean
    open var id: String

    companion object {
        fun getInstance(elem: Element): Modal
        fun init(els: Element, options: ModalOptionsPartial? = definedExternally): Modal
        fun init(els: NodeList, options: ModalOptionsPartial? = definedExternally): Array<Modal>
        fun init(els: JQuery, options: ModalOptionsPartial? = definedExternally): Array<Modal>
        fun init(els: Cash, options: ModalOptionsPartial? = definedExternally): Array<Modal>
    }
}

external interface ModalOptions {
    var opacity: Number
    var inDuration: Number
    var outDuration: Number
    var preventScrolling: Boolean
    var onOpenStart: (`this`: Modal, el: Element) -> Unit
    var onOpenEnd: (`this`: Modal, el: Element) -> Unit
    var onCloseStart: (`this`: Modal, el: Element) -> Unit
    var onCloseEnd: (`this`: Modal, el: Element) -> Unit
    var dismissible: Boolean
    var startingTop: String
    var endingTop: String
}

external interface ModalOptionsPartial {
    var opacity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var outDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var preventScrolling: Boolean?
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
    var dismissible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var startingTop: String?
        get() = definedExternally
        set(value) = definedExternally
    var endingTop: String?
        get() = definedExternally
        set(value) = definedExternally
}
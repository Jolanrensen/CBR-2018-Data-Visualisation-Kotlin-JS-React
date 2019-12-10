@file:JsQualifier("M")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package M

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

external open class Toast : ComponentBase<ToastOptions> {
    open var panning: Boolean
    open var timeRemaining: Number
    open fun dismiss()

    companion object {
        fun getInstance(elem: Element): Toast
        fun dismissAll()
    }
}

external interface ToastOptions {
    var html: String
    var displayLength: Number
    var inDuration: Number
    var outDuration: Number
    var classes: String
    var completeCallback: () -> Unit
    var activationPercent: Number
}

external interface ToastOptionsPartial {
    var html: String?
        get() = definedExternally
        set(value) = definedExternally
    var displayLength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var outDuration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var classes: String?
        get() = definedExternally
        set(value) = definedExternally
    var completeCallback: (() -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var activationPercent: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external fun toast(options: ToastOptionsPartial): Toast
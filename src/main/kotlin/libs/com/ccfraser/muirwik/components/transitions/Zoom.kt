package com.ccfraser.muirwik.components.transitions

import com.ccfraser.muirwik.components.createStyled
import com.ccfraser.muirwik.components.setStyledPropsAndRunHandler
import react.RBuilder
import react.RComponent
import react.RState
import styled.StyledHandler


@JsModule("@material-ui/core/Zoom")
private external val zoomModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val zoomComponent: RComponent<MZoomProps, RState> = zoomModule.default

external interface MZoomProps : MTransitionProps
var MZoomProps.timeout by TransitionDurationDelegate()

fun RBuilder.mZoom(
        show: Boolean = false,
        timeout: TransitionDuration? = null,

        addAsChild: Boolean = true,
        className: String? = null,
        handler: StyledHandler<MZoomProps>? = null) = createStyled(zoomComponent, addAsChild) {
    attrs.show = show
    timeout?.let { attrs.timeout = it }

    setStyledPropsAndRunHandler(className, handler)
}


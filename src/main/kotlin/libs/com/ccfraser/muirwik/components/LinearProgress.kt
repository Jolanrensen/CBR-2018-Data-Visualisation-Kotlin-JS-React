package com.ccfraser.muirwik.components

import react.RBuilder
import react.RComponent
import react.RState
import styled.StyledHandler
import styled.StyledProps


@JsModule("@material-ui/core/LinearProgress")
private external val linearProgressModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val linearProgressComponent: RComponent<MLinearProgressProps, RState> = linearProgressModule.default

@Suppress("EnumEntryName")
enum class MLinearProgressColor {
    primary, secondary
}

@Suppress("EnumEntryName")
enum class MLinearProgressVariant {
    determinate, indeterminate, buffer, query
}

interface MLinearProgressProps : StyledProps {
    var value: Double
    var valueBuffer: Double
}
var MLinearProgressProps.color by EnumPropToString(MLinearProgressColor.values())
var MLinearProgressProps.variant by EnumPropToString(MLinearProgressVariant.values())


fun RBuilder.mLinearProgress(
        value: Double? = null,
        valueBuffer: Double? = null,
        variant: MLinearProgressVariant = MLinearProgressVariant.indeterminate,
        color: MLinearProgressColor = MLinearProgressColor.primary,

        className: String? = null,
        handler: StyledHandler<MLinearProgressProps>? = null) = createStyled(linearProgressComponent) {
    attrs.color = color
    value?.let { attrs.value = it }
    valueBuffer?.let { attrs.valueBuffer = it }
    attrs.variant = variant

    setStyledPropsAndRunHandler(className, handler)
}


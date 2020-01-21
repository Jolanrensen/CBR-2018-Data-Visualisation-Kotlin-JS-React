package libs

import com.soywiz.korma.geom.VectorPath
import io.data2viz.geom.*

fun PathGeom.toVectorPath() = VectorPath().apply {
    for (c in this@toVectorPath.commands) {
        when (c) {
            is MoveTo -> moveTo(c.x, c.y)
            is LineTo -> lineTo(c.x, c.y)
            is RectCmd -> rect(c.x, c.y, c.w, c.h)
            is QuadraticCurveTo -> quadTo(c.cpx, c.cpy, c.x, c.y)
            is BezierCurveTo -> cubicTo(c.cpx1, c.cpy1, c.cpx2, c.cpy2, c.x, c.y)
            is Arc -> arc(c.x, c.y, c.radius, c.startAngle, c.endAngle)
            is ArcTo -> arcTo(c.fromX, c.fromY, c.x, c.y, c.radius)
            is ClosePath -> close()
        }
    }
}
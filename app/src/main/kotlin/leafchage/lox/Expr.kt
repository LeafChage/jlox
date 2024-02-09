package leafchage.lox

abstract class Expr {
    abstract fun <R> accept(v: Visitor<R>): R
}

interface Visitor<R> {
    fun visitBinaryExpr(expr: Binary): R
    fun visitGroupingExpr(expr: Grouping): R
    fun visitLiteralExpr(expr: Literal): R
    fun visitUnaryExpr(expr: Unary): R
}

class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr() {
    override fun <R> accept(v: Visitor<R>): R {
        return v.visitBinaryExpr(this)
    }
}

class Grouping(val expression: Expr) : Expr() {
    override fun <R> accept(v: Visitor<R>): R {
        return v.visitGroupingExpr(this)
    }
}

class Literal(val value: Any?) : Expr() {
    override fun <R> accept(v: Visitor<R>): R {
        return v.visitLiteralExpr(this)
    }
}

class Unary(val operator: Token, val right: Expr) : Expr() {
    override fun <R> accept(v: Visitor<R>): R {
        return v.visitUnaryExpr(this)
    }
}

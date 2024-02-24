package leafchage.lox

abstract class Expr {
    abstract fun <R> accept(v: Visitor<R>): R
    interface Visitor<R> {
        fun visitAssignExpr(expr: Assign): R
        fun visitBinaryExpr(expr: Binary): R
        fun visitCallExpr(expr: Call): R
        fun visitGetExpr(expr: Get): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitLogicalExpr(expr: Logical): R
        fun visitSetExpr(expr: Set): R
        fun visitThisExpr(expr: This): R
        fun visitSuperExpr(expr: Super): R
        fun visitUnaryExpr(expr: Unary): R
        fun visitVariableExpr(expr: Variable): R
    }
    class Assign(val name: Token, val value: Expr) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitAssignExpr(this)
        }
    }

    class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitBinaryExpr(this)
        }
    }

    class Call(val callee: Expr, val paren: Token, val arguments: List<Expr>) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitCallExpr(this)
        }
    }

    class Get(val obj: Expr, val name: Token) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitGetExpr(this)
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

    class Logical(val left: Expr, val operator: Token, val right: Expr) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitLogicalExpr(this)
        }
    }

    class Set(val obj: Expr, val name: Token, val value: Expr) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitSetExpr(this)
        }
    }

    class This(val keyword: Token) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitThisExpr(this)
        }
    }

    class Super(val keyword: Token, val method: Token) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitSuperExpr(this)
        }
    }

    class Unary(val operator: Token, val right: Expr) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitUnaryExpr(this)
        }
    }

    class Variable(val name: Token) : Expr() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitVariableExpr(this)
        }
    }
}

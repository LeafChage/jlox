package leafchage.lox

abstract class Stmt {
    abstract fun <R> accept(v: Visitor<R>): R
    interface Visitor<R> {
        fun visitExpressionStmt(stmt: Expression): R
        fun visitBlockStmt(stmt: Block): R
        fun visitPrintStmt(stmt: Print): R
        fun visitVarStmt(stmt: Var): R
    }
    class Expression(val expression: Expr) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitExpressionStmt(this)
        }
    }

    class Block(val statements: List<Stmt>) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitBlockStmt(this)
        }
    }

    class Print(val expression: Expr) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitPrintStmt(this)
        }
    }

    class Var(val name: Token, val initializer: Expr?) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitVarStmt(this)
        }
    }
}

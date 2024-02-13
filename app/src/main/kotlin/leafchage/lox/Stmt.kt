package leafchage.lox

abstract class Stmt {
    abstract fun <R> accept(v: Visitor<R>): R
    interface Visitor<R> {
        fun visitExpressionStmt(stmt: Expression): R
        fun visitIfStmt(stmt: If): R
        fun visitWhileStmt(stmt: While): R
        fun visitBlockStmt(stmt: Block): R
        fun visitPrintStmt(stmt: Print): R
        fun visitVarStmt(stmt: Var): R
    }
    class Expression(val expression: Expr) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitExpressionStmt(this)
        }
    }

    class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitIfStmt(this)
        }
    }

    class While(val condition: Expr, val body: Stmt) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitWhileStmt(this)
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

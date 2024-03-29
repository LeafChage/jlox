package leafchage.lox

abstract class Stmt {
    abstract fun <R> accept(v: Visitor<R>): R
    interface Visitor<R> {
        fun visitExpressionStmt(stmt: Expression): R
        fun visitFunctionStmt(stmt: Function): R
        fun visitIfStmt(stmt: If): R
        fun visitWhileStmt(stmt: While): R
        fun visitBlockStmt(stmt: Block): R
        fun visitPrintStmt(stmt: Print): R
        fun visitReturnStmt(stmt: Return): R
        fun visitVarStmt(stmt: Var): R
        fun visitClassStmt(stmt: Class): R
    }
    class Expression(val expression: Expr) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitExpressionStmt(this)
        }
    }

    class Function(val name: Token, val params: List<Token>, val body: Stmt.Block) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitFunctionStmt(this)
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

    class Return(val keyword: Token, val value: Expr?) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitReturnStmt(this)
        }
    }

    class Var(val name: Token, val initializer: Expr?) : Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitVarStmt(this)
        }
    }

    class Class(val name: Token, val superClass: Expr.Variable?, val methods: List<Stmt.Function>) :
            Stmt() {
        override fun <R> accept(v: Visitor<R>): R {
            return v.visitClassStmt(this)
        }
    }
}

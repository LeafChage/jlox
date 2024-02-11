package leafchage.lox

public class Interpriter : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {
    private val environment = Environment()
    public fun interpret(statements: List<Stmt>) {
        try {
            for (stmt in statements) {
                execute(stmt)
            }
        } catch (err: RuntimeError) {
            Lox.runtimeError(err)
        }
    }

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    public override fun visitExpressionStmt(stmt: Stmt.Expression): Unit {
        evaluate(stmt.expression)
    }

    public override fun visitPrintStmt(stmt: Stmt.Print): Unit {
        val v = evaluate(stmt.expression)
        System.out.println(stringify(v))
    }

    public override fun visitVarStmt(stmt: Stmt.Var): Unit {
        val v = if (stmt.initializer != null) evaluate(stmt.initializer) else null
        environment.define(stmt.name.lexeme, v)
    }

    public override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val right = evaluate(expr.right)
        val left = evaluate(expr.left)
        return when (expr.operator.type) {
            TokenType.MINUS ->
                    castNumberOrFailed(expr.operator, left) -
                            castNumberOrFailed(expr.operator, right)
            TokenType.STAR ->
                    castNumberOrFailed(expr.operator, left) *
                            castNumberOrFailed(expr.operator, right)
            TokenType.SLASH ->
                    castNumberOrFailed(expr.operator, left) /
                            castNumberOrFailed(expr.operator, right)
            TokenType.PLUS -> {
                if (right is Double && left is Double) {
                    left + right
                } else if (right is String && left is String) {
                    String.format("%s%s", left, right)
                } else {
                    throw RuntimeError(expr.operator, "Operand must be two numbers or two strings")
                }
            }
            TokenType.BANG_EQUAL -> !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> isEqual(left, right)
            TokenType.GREATER ->
                    castNumberOrFailed(expr.operator, left) >
                            castNumberOrFailed(expr.operator, right)
            TokenType.GREATER_EQUAL ->
                    castNumberOrFailed(expr.operator, left) >=
                            castNumberOrFailed(expr.operator, right)
            TokenType.LESS ->
                    castNumberOrFailed(expr.operator, left) <
                            castNumberOrFailed(expr.operator, right)
            TokenType.LESS_EQUAL ->
                    castNumberOrFailed(expr.operator, left) <=
                            castNumberOrFailed(expr.operator, right)
            else -> {
                throw UnreachableException()
            }
        }
    }

    public override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    public override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    public override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> -castNumberOrFailed(expr.operator, right)
            else -> throw UnreachableException()
        }
    }

    public override fun visitVariableExpr(expr: Expr.Variable): Any? = environment.get(expr.name)

    private fun castNumberOrFailed(ope: Token, v: Any?): Double {
        if (v == null || v !is Double) {
            throw RuntimeError(ope, "Operand must be a number")
        }
        return v
    }

    private fun isTruthy(obj: Any?): Boolean {
        return if (obj == null) {
            false
        } else if (obj is Boolean) {
            obj
        } else {
            true
        }
    }

    private fun isEqual(left: Any?, right: Any?): Boolean {
        return if (left == null && right == null) {
            true
        } else if (left == null) {
            false
        } else {
            left.equals(right)
        }
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) {
            return "null"
        }
        if (obj is Double) {
            val text = obj.toString()
            return if (text.endsWith(".0")) text.substring(0, text.length - 2) else text
        }
        return obj.toString()
    }
}

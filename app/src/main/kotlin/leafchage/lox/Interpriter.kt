package leafchage.lox

import leafchage.lox.native.*

public class Interpriter : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {
    private val locals = hashMapOf<Expr, Int>()

    public var globals = Environment()
        private set
    private var environment = globals

    public constructor() {
        globals.define("clock", Clock())
    }

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

    public override fun visitWhileStmt(stmt: Stmt.While): Unit {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
    }

    public override fun visitIfStmt(stmt: Stmt.If): Unit {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else {
            if (stmt.elseBranch != null) {
                execute(stmt.elseBranch)
            }
        }
    }

    public override fun visitReturnStmt(stmt: Stmt.Return): Unit {
        val value = if (stmt.value != null) evaluate(stmt.value) else null
        throw Return(value)
    }

    public override fun visitFunctionStmt(stmt: Stmt.Function): Unit {
        val fn = LoxFunction(stmt, environment)
        environment.define(stmt.name.lexeme, fn)
    }

    public override fun visitBlockStmt(stmt: Stmt.Block): Unit {
        executeBlock(stmt, Environment(environment))
    }

    public override fun visitExpressionStmt(stmt: Stmt.Expression): Unit {
        evaluate(stmt.expression)
    }

    public override fun visitPrintStmt(stmt: Stmt.Print): Unit {
        val v = evaluate(stmt.expression)
        System.out.println(stringify(v))
    }

    public override fun visitVarStmt(stmt: Stmt.Var): Unit {
        if (stmt.initializer == null) {
            environment.define(stmt.name.lexeme)
        } else {
            environment.define(stmt.name.lexeme, evaluate(stmt.initializer))
        }
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

    public override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)
        val arguments = expr.arguments.map({ arg -> evaluate(arg) })
        if (callee !is LoxCallable) {
            throw RuntimeError(expr.paren, "Can only call functions and classes.")
        }
        if (arguments.size != callee.arity()) {
            throw RuntimeError(
                    expr.paren,
                    String.format(
                            "Expected %d arguments but got %d.",
                            callee.arity(),
                            arguments.size
                    )
            )
        }
        return callee.call(this, arguments)
    }

    public override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    public override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    public override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)
        return when (expr.operator.type) {
            TokenType.OR ->
                    if (isTruthy(left)) {
                        left
                    } else {
                        evaluate(expr.right)
                    }
            TokenType.AND ->
                    if (!isTruthy(left)) {
                        left
                    } else {
                        evaluate(expr.right)
                    }
            else -> {
                throw UnreachableException()
            }
        }
    }

    public override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> -castNumberOrFailed(expr.operator, right)
            else -> throw UnreachableException()
        }
    }

    public override fun visitVariableExpr(expr: Expr.Variable): Any? =
            lookUpVariable(expr.name, expr)

    public override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)

        val distance = locals.get(expr)
        if (distance != null) {
            environment.assignAt(distance, expr.name, value)
        } else {
            globals.assign(expr.name, value)
        }
        return value
    }

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

    public fun executeBlock(block: Stmt.Block, innerEnv: Environment): Unit {
        val outerEnv = environment
        try {
            this.environment = innerEnv
            for (stmt in block.statements) {
                execute(stmt)
            }
        } finally {
            this.environment = outerEnv
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

    public fun resolve(expr: Expr, depth: Int) = locals.put(expr, depth)

    private fun lookUpVariable(name: Token, expr: Expr): Any? {
        val distance = locals.get(expr)
        return if (distance != null) environment.getAt(distance, name) else globals.get(name)
    }
}

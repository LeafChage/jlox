package leafchage.lox

import java.util.Stack
import kotlin.collections.hashMapOf

private enum class VariableType {
    Declared,
    Defined,

    // 一度も参照されていない変数はresolve error
    Refered,
}

private data class Variable(val token: Token?, val type: VariableType) {}

private enum class FunctionType {
    None,
    Function,
    Method,
    Initializer,
}

private enum class ClassType {
    None,
    Class,
}

// 構文木を作成した後にインタプリタ実行の前に木を巡回して、
// 全ての変数の参照先を解決する
public class Resolver(val interpriter: Interpriter) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {
    private var scopes: Stack<MutableMap<String, Variable>> = Stack()
    private var currentFunction = FunctionType.None
    private var currentClass = ClassType.None

    public override fun visitClassStmt(stmt: Stmt.Class): Unit {
        val enclosingClass = this.currentClass
        currentClass = ClassType.Class

        declare(stmt.name)
        define(stmt.name)

        beginScope()
        scopes.peek().put("this", Variable(null, VariableType.Refered))
        for (method in stmt.methods) {
            val delaration =
                    if (method.name.lexeme.equals("init")) {
                        FunctionType.Initializer
                    } else {
                        FunctionType.Method
                    }

            resolveFunction(method, delaration)
        }
        endScope()

        this.currentClass = enclosingClass
    }
    public override fun visitExpressionStmt(stmt: Stmt.Expression): Unit {
        resolve(stmt.expression)
    }
    public override fun visitFunctionStmt(stmt: Stmt.Function): Unit {
        declare(stmt.name)
        define(stmt.name)
        resolveFunction(stmt, FunctionType.Function)
    }
    public override fun visitIfStmt(stmt: Stmt.If): Unit {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch)
        }
    }
    public override fun visitWhileStmt(stmt: Stmt.While): Unit {
        resolve(stmt.condition)
        resolve(stmt.body)
    }
    public override fun visitBlockStmt(stmt: Stmt.Block): Unit {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }
    public override fun visitPrintStmt(stmt: Stmt.Print): Unit {
        resolve(stmt.expression)
    }
    public override fun visitReturnStmt(stmt: Stmt.Return): Unit {
        if (currentFunction == FunctionType.None) {
            Lox.error(stmt.keyword, "Can't return from top-level code.")
        }

        if (stmt.value != null) {
            if (currentFunction == FunctionType.Initializer) {
                Lox.error(stmt.keyword, "Can't return a value from an initializer.")
            }
            resolve(stmt.value)
        }
    }
    public override fun visitVarStmt(stmt: Stmt.Var): Unit {
        declare(stmt.name)
        if (stmt.initializer != null) {
            resolve(stmt.initializer)
        }
        define(stmt.name)
    }
    public override fun visitAssignExpr(expr: Expr.Assign): Unit {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
    }
    public override fun visitGetExpr(expr: Expr.Get): Unit {
        resolve(expr.obj)
    }
    public override fun visitBinaryExpr(expr: Expr.Binary): Unit {
        resolve(expr.left)
        resolve(expr.right)
    }
    public override fun visitCallExpr(expr: Expr.Call): Unit {
        resolve(expr.callee)
        for (arg in expr.arguments) {
            resolve(arg)
        }
    }
    public override fun visitGroupingExpr(expr: Expr.Grouping): Unit {
        resolve(expr.expression)
    }
    public override fun visitLiteralExpr(expr: Expr.Literal): Unit {
        // do nothing
    }
    public override fun visitLogicalExpr(expr: Expr.Logical): Unit {
        resolve(expr.left)
        resolve(expr.right)
    }

    public override fun visitSetExpr(expr: Expr.Set): Unit {
        resolve(expr.value)
        resolve(expr.obj)
    }
    public override fun visitUnaryExpr(expr: Expr.Unary): Unit {
        resolve(expr.right)
    }
    public override fun visitThisExpr(expr: Expr.This): Unit {
        if (currentClass == ClassType.None) {
            Lox.error(expr.keyword, "Can't use 'this' outside of a class")
        }
        resolveLocal(expr, expr.keyword)
    }
    public override fun visitVariableExpr(expr: Expr.Variable): Unit {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme)?.type == VariableType.Defined
        ) {
            Lox.error(expr.name, "Can't read local variable in its own initializer.")
        }
        resolveLocal(expr, expr.name)
    }

    private fun beginScope() {
        scopes.push(hashMapOf<String, Variable>())
    }

    private fun endScope() {
        val scope = scopes.pop()
        for ((_, value) in scope) {
            if (value.type != VariableType.Refered && value.token != null) {
                Lox.error(value.token, "must't declare unrefered variable.")
            }
        }
    }

    private fun declare(name: Token) {
        if (scopes.isEmpty()) {
            return
        }

        var scope = scopes.peek()
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already a variable with this name in this scope.")
        }
        scope.put(name.lexeme, Variable(name, VariableType.Declared))
    }

    private fun define(name: Token) {
        if (scopes.isEmpty()) {
            return
        }

        scopes.peek().put(name.lexeme, Variable(name, VariableType.Defined))
    }
    private fun resolveLocal(expr: Expr, name: Token) {
        for ((i, scope) in scopes.reversed().withIndex()) {
            if (scope.containsKey(name.lexeme)) {
                // 参照されたのでReferredに変更
                scope.put(name.lexeme, Variable(name, VariableType.Refered))
                interpriter.resolve(expr, i)
                return
            }
        }
    }

    private fun resolveFunction(function: Stmt.Function, type: FunctionType) {
        var enclosingFunction = currentFunction
        currentFunction = type
        beginScope()
        for (param in function.params) {
            declare(param)
            define(param)
        }
        resolve(function.body)
        endScope()
        currentFunction = enclosingFunction
    }

    public fun resolve(statements: List<Stmt>) {
        for (stmt in statements) {
            resolve(stmt)
        }
    }
    private fun resolve(statement: Stmt) {
        statement.accept(this)
    }
    private fun resolve(expr: Expr) {
        expr.accept(this)
    }
}

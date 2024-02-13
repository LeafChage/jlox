package leafchage.lox

import kotlin.collections.mutableListOf

public class Parser(val tokens: List<Token>) {
    private val MAX_ARGUMENTS_SIZE = 255
    class ParserError : RuntimeException() {}

    var current = 0

    public fun parse(): List<Stmt> {
        val statements = ArrayList<Stmt>()
        while (!isAtEnd()) {
            val s = declaration()
            if (s != null) {
                statements.add(s)
            }
        }
        return statements
    }

    private fun declaration(): Stmt? {
        try {
            return if (match(TokenType.VAR)) varDeclaration()
            else if (match(TokenType.FUN)) function("function") else statement()
        } catch (err: ParserError) {
            synchronize()
            return null
        }
    }

    private fun varDeclaration(): Stmt? {
        val name = consume(TokenType.INDETIFIER, "Expect variable name.")
        val init = if (match(TokenType.EQUAL)) expression() else null
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
        return Stmt.Var(name, init)
    }

    private fun function(kind: String): Stmt.Function {
        val name = consume(TokenType.INDETIFIER, String.format("Expect %s name.", kind))
        consume(TokenType.LEFT_PAREN, String.format("Expect '(' after %s name.", kind))
        var parameters = mutableListOf<Token>()
        if (!check(TokenType.RIGHT_PAREN)) {
            parameters.add(consume(TokenType.INDETIFIER, "Expect parameter name."))
            while (match(TokenType.COMMA)) {
                if (parameters.size >= MAX_ARGUMENTS_SIZE) {
                    error(peek(), "Can't have more than 255 parameters.")
                }
                parameters.add(consume(TokenType.INDETIFIER, "Expect parameter name."))
            }
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
        consume(TokenType.LEFT_BRACE, String.format("Expect '{' before %s body.", kind))
        val body = block()
        return Stmt.Function(name, parameters, body)
    }

    private fun statement(): Stmt =
            if (match(TokenType.PRINT)) printStatement()
            else if (match(TokenType.IF)) ifStatement()
            else if (match(TokenType.WHILE)) whileStatement()
            else if (match(TokenType.FOR)) forStatement()
            else if (match(TokenType.LEFT_BRACE)) block() else expressionStatement()

    private fun ifStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Exprect '(' after 'if'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Exprect ')' after if condition.")

        val thenStatement = statement()
        val elseStatement = if (match(TokenType.ELSE)) statement() else null
        return Stmt.If(condition, thenStatement, elseStatement)
    }

    private fun whileStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Exprect '(' after 'while'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Exprect ')' after while condition.")

        val bodyStatement = statement()
        return Stmt.While(condition, bodyStatement)
    }

    private fun forStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.")
        val initializer =
                if (match(TokenType.SEMICOLON)) {
                    null
                } else if (match(TokenType.VAR)) {
                    varDeclaration()
                } else {
                    expressionStatement()
                }
        val condition =
                if (match(TokenType.SEMICOLON)) {
                    null
                } else {
                    expression()
                }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.")
        val increment =
                if (match(TokenType.RIGHT_PAREN)) {
                    null
                } else {
                    expression()
                }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.")

        var body = statement()
        if (increment != null) {
            body = Stmt.Block(listOf(body, Stmt.Expression(increment)))
        }
        body = Stmt.While(if (condition != null) condition else Expr.Literal(true), body)
        if (initializer != null) {
            body = Stmt.Block(listOf(initializer, body))
        }
        return body
    }

    fun block(): Stmt.Block {
        var statements = mutableListOf<Stmt>()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            val d = declaration()
            if (d != null) {
                statements.add(d)
            }
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block")
        return Stmt.Block(statements)
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value")
        return Stmt.Print(value)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression")
        return Stmt.Expression(expr)
    }

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = or()
        if (match(TokenType.EQUAL)) {
            val equal = previous()
            val value = assignment()
            if (expr is Expr.Variable) {
                val name = expr.name
                return Expr.Assign(name, value)
            }
            error(equal, "Invalid assignment target.")
        }

        return expr
    }

    private fun or(): Expr {
        var expr = and()
        while (match(TokenType.OR)) {
            val operator = previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    private fun and(): Expr {
        var expr = equality()
        while (match(TokenType.AND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    private fun equality(): Expr {
        var expr = comparsion()
        while (match(
                TokenType.BANG_EQUAL,
                TokenType.EQUAL_EQUAL,
        )) {
            val operator = previous()
            val right = comparsion()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparsion(): Expr {
        var expr = term()
        while (match(
                TokenType.GREATER,
                TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL
        )) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return call()
    }

    private fun call(): Expr {
        var expr = primary()
        while (match(TokenType.LEFT_PAREN)) {
            expr = finishCall(expr)
        }
        return expr
    }

    private fun finishCall(calee: Expr): Expr {
        var arguments = mutableListOf<Expr>()
        if (!check(TokenType.RIGHT_PAREN)) {
            arguments.add(expression())
            while (match(TokenType.COMMA)) {
                if (arguments.size >= MAX_ARGUMENTS_SIZE) {
                    error(peek(), "Can't have more than 255 arguments.")
                }
                arguments.add(expression())
            }
        }

        val paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")
        return Expr.Call(calee, paren, arguments)
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE)) {
            return Expr.Literal(false)
        }
        if (match(TokenType.TRUE)) {
            return Expr.Literal(true)
        }
        if (match(TokenType.NIL)) {
            return Expr.Literal(null)
        }
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression")
            return Expr.Grouping(expr)
        }
        if (match(TokenType.INDETIFIER)) {
            return Expr.Variable(previous())
        }
        throw error(peek(), "Expect expression.")
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean = if (isAtEnd()) false else peek().type == type

    private fun isAtEnd() = peek().type == TokenType.EOF
    private fun peek() = tokens.get(current)
    private fun previous() = tokens.get(current - 1)
    private fun advance(): Token {
        if (!isAtEnd()) {
            current++
        }
        return previous()
    }

    // check next expected token.
    private fun consume(type: TokenType, msg: String): Token {
        if (check(type)) {
            return advance()
        }
        throw error(peek(), msg)
    }

    private fun error(token: Token, msg: String): ParserError {
        Lox.error(token, msg)
        return ParserError()
    }

    // when parser exception happened, move position to next expression
    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) {
                return
            }
            when (peek().type) {
                TokenType.CLASS,
                TokenType.FOR,
                TokenType.FUN,
                TokenType.IF,
                TokenType.PRINT,
                TokenType.RETURN,
                TokenType.VAR,
                TokenType.WHILE -> return
                else -> {
                    advance()
                }
            }
        }
    }
}

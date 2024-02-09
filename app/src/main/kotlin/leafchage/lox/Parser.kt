package leafchage.lox

public class Parser(val tokens: List<Token>) {
    class ParserError : RuntimeException() {}

    var current = 0

    public fun parse(): Expr? {
        try {
            return expression()
        } catch (e: ParserError) {
            return null;
        }
    }

    private fun expression(): Expr {
        return equality()
    }

    private fun equality(): Expr {
        var expr = comparsion()
        while (match(
                TokenType.BANG_EQUAL,
                TokenType.EQUAL_EQUAL,
        )) {
            val operator = previous()
            val right = comparsion()
            expr = Binary(expr, operator, right)
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
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE)) {
            return Literal(false)
        }
        if (match(TokenType.TRUE)) {
            return Literal(true)
        }
        if (match(TokenType.NIL)) {
            return Literal(null)
        }
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Literal(previous().literal)
        }
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression")
            return Grouping(expr)
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

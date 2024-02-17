package leafchage.lox

public class Scanner(val source: String) {
    private var tokens = ArrayList<Token>()
    private var start = 0
    private var current = 0
    private var line = 1
    private val keywords =
            mapOf(
                    Pair("and", TokenType.AND),
                    Pair("or", TokenType.OR),
                    Pair("if", TokenType.IF),
                    Pair("else", TokenType.ELSE),
                    Pair("true", TokenType.TRUE),
                    Pair("false", TokenType.FALSE),
                    Pair("class", TokenType.CLASS),
                    Pair("fun", TokenType.FUN),
                    Pair("return", TokenType.RETURN),
                    Pair("super", TokenType.SUPER),
                    Pair("this", TokenType.THIS),
                    Pair("for", TokenType.FOR),
                    Pair("while", TokenType.WHILE),
                    Pair("print", TokenType.PRINT),
                    Pair("var", TokenType.VAR),
                    Pair("nil", TokenType.NIL),
            )

    public fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            '*' -> addToken(TokenType.STAR)
            ';' -> addToken(TokenType.SEMICOLON)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> {
                // comment last until new line
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance()
                    }
                }
            }
            ' ', '\r', '\t' -> {
                // ignore white space
            }
            '\n' -> {
                line++
            }
            '"' -> string()
            else -> {
                if (c.isDigit()) {
                    number()
                } else if (c.isAlpha()) {
                    identifier()
                } else {
                    Lox.error(line, "Unexpected")
                }
            }
        }
    }

    private fun identifier() {
        while (peek().isAlphaNumeric()) {
            advance()
        }
        val text = source.substring(start, current)
        val type = keywords.get(text)
        addToken(type ?: TokenType.INDETIFIER)
    }

    // check number
    // and insert number token to list
    private fun number() {
        while (peek().isDigit()) {
            advance()
        }

        // check decimal point
        if (peek() == '.' && peekNext().isDigit()) {
            advance()

            // check decimal point
            while (peek().isDigit()) {
                advance()
            }
        }
        val value = source.substring(start, current).toDouble()
        addToken(TokenType.NUMBER, value)
    }

    // check until next double quotation
    // insert string token to list
    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++
            }
            advance()
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        advance()
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) {
            return false
        }
        if (source.get(current) != expected) {
            return false
        }
        advance()
        return true
    }

    private fun isAtEnd() = current >= source.length

    // check next
    private fun peek() = if (isAtEnd()) '\u0000' else source.get(current)

    // check next to next
    private fun peekNext(): Char =
            if (current + 1 > source.length) '\u0000' else source.get(current + 1)

    // move next
    private fun advance(): Char = source.get(current++)

    private fun addToken(type: TokenType) = addToken(type, null)

    private fun addToken(type: TokenType, literal: Any?) {
        val text = this.source.substring(this.start, this.current)
        this.tokens.add(Token(type, text, literal, line))
    }
}

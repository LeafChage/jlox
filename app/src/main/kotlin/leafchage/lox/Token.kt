package leafchage.lox

class Token(val t: TokenType, val lexeme: String, val literal: Any?, val line: Int) {
    public override fun toString(): String {
        return this.t.toString() + " " + this.lexeme + " " + this.literal
    }
}

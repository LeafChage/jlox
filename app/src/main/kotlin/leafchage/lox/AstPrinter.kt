package leafchage.lox

class AstPrinter : Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    override public fun visitBinaryExpr(expr: Binary): String =
            parentize(expr.operator.lexeme, expr.left, expr.right)

    override public fun visitGroupingExpr(expr: Grouping): String =
            parentize("group", expr.expression)

    override public fun visitLiteralExpr(expr: Literal): String =
            if (expr.value == null) "nil" else expr.value.toString()

    override public fun visitUnaryExpr(expr: Unary): String =
            parentize(expr.operator.lexeme, expr.right)

    private fun parentize(name: String, vararg exprs: Expr): String {
        var builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr.accept(this))
        }

        builder.append(")")
        return builder.toString()
    }
}

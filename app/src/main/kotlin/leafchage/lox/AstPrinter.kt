package leafchage.lox

// class AstPrinter : Expr.Visitor<String> {
//     fun print(expr: Expr): String {
//         return expr.accept(this)
//     }
//
//     public override fun visitBinaryExpr(expr: Expr.Binary): String =
//             parentize(expr.operator.lexeme, expr.left, expr.right)
//
//     public override fun visitGroupingExpr(expr: Expr.Grouping): String = parentize("group", expr.expression)
//
//     public override fun visitLiteralExpr(expr: Expr.Literal): String =
//             if (expr.value == null) "nil" else expr.value.toString()
//
//     public override fun visitUnaryExpr(expr: Expr.Unary): String = parentize(expr.operator.lexeme, expr.right)
//
//     private fun parentize(name: String, vararg exprs: Expr): String {
//         var builder = StringBuilder()
//         builder.append("(").append(name)
//         for (expr in exprs) {
//             builder.append(" ")
//             builder.append(expr.accept(this))
//         }
//
//         builder.append(")")
//         return builder.toString()
//     }
// }

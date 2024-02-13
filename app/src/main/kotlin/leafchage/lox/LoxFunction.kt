package leafchage.lox

public class LoxFunction(var declaration: Stmt.Function) : LoxCallable {
    public override fun arity(): Int = declaration.params.size

    public override fun call(interpriter: Interpriter, arguments: List<Any?>): Any? {
        // 関数は実行ごとに独自の環境を生成する
        val environment = Environment(interpriter.globals)
        for (i in 0..<declaration.params.size) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i))
        }

        interpriter.executeBlock(declaration.body, environment)
        return null
    }

    public override fun toString() = String.format("<fn %s>", declaration.name.lexeme)
}

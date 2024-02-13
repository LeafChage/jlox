package leafchage.lox

public class LoxFunction(var declaration: Stmt.Function, var closure: Environment) : LoxCallable {
    public override fun arity(): Int = declaration.params.size

    public override fun call(interpriter: Interpriter, arguments: List<Any?>): Any? {
        // 関数は実行ごとに独自の環境を生成する
        val environment = Environment(closure)
        for (i in 0 ..< declaration.params.size) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i))
        }

        try {
            interpriter.executeBlock(declaration.body, environment)
        } catch (e: Return) {
            return e.value
        }
        return null
    }

    public override fun toString() = String.format("<fn %s>", declaration.name.lexeme)
}

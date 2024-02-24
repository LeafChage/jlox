package leafchage.lox

public class LoxFunction(
        var declaration: Stmt.Function,
        var closure: Environment,
        val isInitializer: Boolean
) : LoxCallable {
    public override fun arity(): Int = declaration.params.size

    public override fun call(interpriter: Interpriter, arguments: List<Any?>): Any? {
        // 関数は実行ごとに独自の環境を生成する
        val env = Environment(closure)
        for (i in 0 ..< declaration.params.size) {
            env.define(declaration.params.get(i).lexeme, arguments.get(i))
        }

        try {
            interpriter.executeBlock(declaration.body, env)
        } catch (returnValue: Return) {
            if (isInitializer) return closure.getAt(0, "this")
            return returnValue.value
        }

        if (isInitializer) {
            // initを呼ぶと常にthisを返す
            // initを直接呼んで再初期化することはできない
            return closure.getAt(0, "this")
        }

        return null
    }

    public fun bind(instance: LoxInstance): LoxFunction {
        val env = Environment(closure)
        env.define("this", instance)
        return LoxFunction(declaration, env, isInitializer)
    }

    public override fun toString() = String.format("<fn %s>", declaration.name.lexeme)
}

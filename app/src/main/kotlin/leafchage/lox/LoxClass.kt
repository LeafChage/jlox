package leafchage.lox

public class LoxClass(
        val name: String,
        val superClass: LoxClass?,
        var methods: Map<String, LoxFunction>
) : LoxCallable {
    public override fun arity(): Int = findMethod("init")?.arity() ?: 0

    public override fun call(interpriter: Interpriter, arguments: List<Any?>): LoxInstance {
        val instance = LoxInstance(this)

        val initializer = findMethod("init")
        if (initializer != null) {
            initializer.bind(instance).call(interpriter, arguments)
        }

        return instance
    }

    public override fun toString(): String = name

    public fun findMethod(name: String): LoxFunction? {
        val method = methods.get(name)

        if (method != null) {
            return method
        }

        return superClass?.findMethod(name)
    }
}

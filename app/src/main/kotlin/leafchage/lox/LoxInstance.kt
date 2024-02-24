package leafchage.lox

import kotlin.collections.mutableMapOf

public class LoxInstance(val klass: LoxClass) {
    private var fields = mutableMapOf<String, Any?>()

    public override fun toString(): String = String.format("%s instance", klass.name)

    public fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme)
        }
        val method = klass.findMethod(name.lexeme)
        if (method != null) {
            return method.bind(this)
        }

        throw RuntimeError(name, String.format("Undefined property '%s'.", name.lexeme))
    }

    public fun set(name: Token, value: Any?) {
        fields.put(name.lexeme, value)
    }
}

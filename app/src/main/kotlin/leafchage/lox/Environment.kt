package leafchage.lox

public class Environment {
    var values = hashMapOf<String, Any?>()

    public fun define(name: String, value: Any?) {
        values.put(name, value)
    }

    public fun get(name: Token): Any {
        if (values.contains(name.lexeme)) {
            return values.get(name.lexeme)!!
        }
        throw RuntimeError(name, String.format("Undefined variable '%s'", name.lexeme))
    }
}

package leafchage.lox

data class EValue(val value: Any?, val init: Boolean) {}

public class Environment(
        // この環境よりも上のScopeを含める
        // この環境に変数が存在しない場合上のScopeを調査する
        // globalScopeは上が存在しないのでNullになる
        val enclosing: Environment? = null
) {
    var values = hashMapOf<String, EValue>()

    public fun define(name: String) {
        values.put(name, EValue(null, false))
    }
    public fun define(name: String, value: Any?) {
        values.put(name, EValue(value, true))
    }

    public fun get(name: Token): Any? {
        if (values.contains(name.lexeme)) {
            val value = values.get(name.lexeme)
            if (value == null) {
                return null
            } else if (value.init) {
                return value.value
            }
            throw RuntimeError(name, String.format("Not initialized variable '%s'", name.lexeme))
        }
        if (enclosing != null) {
            return enclosing.get(name)
        }
        throw RuntimeError(name, String.format("Undefined variable '%s'", name.lexeme))
    }

    public fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, EValue(value, true))
            return
        }
        if (enclosing != null) {
            return enclosing.assign(name, value)
        }

        throw RuntimeError(name, String.format("Undefined variable '%s'", name.lexeme))
    }
}

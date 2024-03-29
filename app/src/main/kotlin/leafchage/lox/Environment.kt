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

    // Distanceの分だけ広げた範囲のScopeを取得してその環境から探す
    public fun getAt(distance: Int, name: Token) = getAt(distance, name.lexeme)
    public fun getAt(distance: Int, name: String) = ancestor(distance).values.get(name)?.value

    private fun ancestor(distance: Int): Environment {
        var env = this
        for (i in 0 ..< distance) {
            // distanceの分だけScopeを広げていく
            env = env.enclosing!!
        }
        return env
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

    public fun assignAt(distance: Int, name: Token, value: Any?) {
        ancestor(distance).values.put(name.lexeme, EValue(value, true))
    }

    public fun debug(level: Int = 0) {
        for (i in 0 ..< level) {
            print("\t")
        }
        println(values)
        if (enclosing != null) {
            enclosing.debug(level + 1)
        }
    }
}

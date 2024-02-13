package leafchage.lox

public interface LoxCallable {
    fun arity(): Int
    fun call(interpriter: Interpriter, arguments: List<Any?>): Any?
}

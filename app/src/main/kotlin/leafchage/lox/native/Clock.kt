package leafchage.lox.native

import leafchage.lox.Interpriter
import leafchage.lox.LoxCallable

public class Clock : LoxCallable {
    public override fun arity() = 0

    public override fun call(interpriter: Interpriter, arguments: List<Any?>): Any? =
            System.currentTimeMillis()

    public override fun toString() = "<native fn>"
}

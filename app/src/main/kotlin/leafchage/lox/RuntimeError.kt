package leafchage.lox

public class RuntimeError(val token: Token, val msg: String) : RuntimeException(msg) {}

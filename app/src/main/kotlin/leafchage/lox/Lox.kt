package leafchage.lox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val app = Lox()
    if (args.size > 1) {
        System.out.println("Usage: jlox [script]")
        System.exit(64)
    } else if (args.size == 1) {
        app.runFile(args[0])
    } else {
        app.runPrompt()
    }
}

public class Lox {
    companion object {
        var hadError = false
        var hadRuntimeError = false
        val interpreter = Interpriter()
        public fun error(token: Token, msg: String) {
            if (token.type == TokenType.EOF) {
                report(token.line, "at end", msg)
            } else {
                report(token.line, String.format(" at '%s'", token.lexeme), msg)
            }
        }

        public fun runtimeError(err: RuntimeError) {
            System.err.println(String.format("[line %d] Error: %s", err.token.line, err.message))
            hadRuntimeError = true
        }

        public fun error(line: Int, msg: String) {
            report(line, "", msg)
        }

        public fun report(line: Int, where: String, msg: String) {
            System.err.println(String.format("[line %d] Error%s: %s", line, where, msg))
            hadError = true
        }
    }

    public fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))
        if (hadError) {
            System.exit(65)
        }
        if (hadRuntimeError) {
            System.exit(70)
        }
    }

    public fun runPrompt() {
        while (true) {
            System.out.println("> ")
            val line = readLine()
            if (line == null) {
                break
            } else {
                run(line)
                hadError = false
            }
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()
        val parser = Parser(tokens)
        val statements = parser.parse()
        if (hadError) {
            // parse error
            println("parser error")
            return
        }

        val resolver = Resolver(interpreter)
        resolver.resolve(statements)
        if (hadError) {
            // resolve error
            println("resolve error")
            return
        }

        interpreter.interpret(statements)
    }
}

package leafchage.lox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

var hadError = false

fun error(line: Int, msg: String) {
    report(line, "", msg)
}

fun report(line: Int, where: String, msg: String) {
    System.err.println(String.format("[line %d ] Error%s: %s", line, where, msg))
    hadError = true
}

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
    public fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))
        if (hadError) {
            System.exit(65)
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
                hadError = true
            }
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        for (token in tokens) {
            System.out.println(token)
        }
    }
}

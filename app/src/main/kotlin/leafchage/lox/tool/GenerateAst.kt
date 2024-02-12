package leafchage.lox.tool

import java.io.PrintWriter

class Field(val value: String, val type: String) {
    public fun showCode(): String = String.format("%s: %s", value, type)
}

class Ast(val name: String, vararg fields: Field) {
    val fields: List<Field>
    init {
        this.fields = fields.toList()
    }
    public fun showConstructor(): String =
            fields.map({ f: Field -> String.format("val %s", f.showCode()) }).joinToString(", ")
}

fun main(args: Array<String>) {
    if (args.count() == 0) {
        System.err.println("Usage: generate_ast <output directory>")
        System.exit(64)
    }
    val output = args[0]
    defineAst(
            output,
            "Expr",
            Ast(
                    "Assign",
                    Field("name", "Token"),
                    Field("value", "Expr"),
            ),
            Ast(
                    "Binary",
                    Field("left", "Expr"),
                    Field("operator", "Token"),
                    Field("right", "Expr"),
            ),
            Ast(
                    "Grouping",
                    Field("expression", "Expr"),
            ),
            Ast(
                    "Literal",
                    Field("value", "Any?"),
            ),
            Ast(
                    "Unary",
                    Field("operator", "Token"),
                    Field("right", "Expr"),
            ),
            Ast(
                    "Variable",
                    Field("name", "Token"),
            ),
    )

    defineAst(
            output,
            "Stmt",
            Ast(
                    "Expression",
                    Field("expression", "Expr"),
            ),
            Ast(
                    "Block",
                    Field("statements", "List<Stmt>"),
            ),
            Ast(
                    "Print",
                    Field("expression", "Expr"),
            ),
            Ast(
                    "Var",
                    Field("name", "Token"),
                    Field("initializer", "Expr?"),
            ),
    )
}

val packageName = "leafchage.lox"

fun defineAst(outputDir: String, baseName: String, vararg types: Ast) {
    val path = String.format("%s/%s.kt", outputDir, baseName)
    val writer = PrintWriter(path, "UTF-8")

    writer.println(String.format("package %s", packageName))
    writer.println()
    writer.println(String.format("abstract class %s {", baseName))
    writer.println("abstract fun<R> accept(v: Visitor<R>): R")
    defineVisitor(writer, baseName, types.asList())
    for (type in types) {
        defineType(writer, baseName, type)
        writer.println()
    }
    writer.println("}")

    writer.close()
}

fun defineType(writer: PrintWriter, baseName: String, type: Ast) {
    writer.println(
            String.format(
                    "class %s(%s) : %s() {",
                    type.name,
                    type.showConstructor(),
                    baseName,
            )
    )
    writer.println("override fun <R> accept(v: Visitor<R>): R {")
    writer.println(String.format("return v.visit%s%s(this)", type.name, baseName))
    writer.println("}")
    writer.println("}")
}

fun defineVisitor(writer: PrintWriter, baseName: String, types: List<Ast>) {
    writer.println("interface Visitor<R>{")
    for (type in types) {
        writer.println(
                String.format(
                        "fun %s(%s): R",
                        String.format("visit%s%s", type.name, baseName),
                        String.format("%s: %s", baseName.lowercase(), type.name)
                )
        )
    }
    writer.println("}")
}

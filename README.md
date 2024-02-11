# Lox

## BNF
```
program     -> statement* EOF;
declaration -> varDecl | statement;
varDecl     -> "var" IDETIFIER ( "=" expression )? ";";
statement   -> exprStmt | printStmt;
exprStmt    -> expression ";";
printStmt   -> "print" expression ";";
expression  -> equality;
equality    -> comparison ( ( "!=" | "==" ) comparison )*;
comparison  -> term ( ( ">" | ">=" | "<" | "<=" ) term )*;
term        -> factor ( ( "-" | "+" ) factor )*;
factor      -> unary ( ( "/" | "*" ) unary )*;
unary       -> ( "!" | "-" ) unary | primary;
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDETIFIER;
```


# Lox

## Reference
[インタプリタの作り方 －言語設計／開発の基本と2つの方式による実装](https://book.impress.co.jp/books/1122101087)

## BNF
```
program     -> statement* EOF;
declaration -> classDecl | varDecl | funDecl | statement;
classDecl   -> "class" IDETIFIER  "{" function* "}";
funDecl     -> "fun" function ;
function    -> IDETIFIER "(" parameters? ")" block;
parameters  -> IDETIFIER ( ","  IDETIFIER )* ;
varDecl     -> "var" IDETIFIER ( "=" expression )? ";";
statement   -> exprStmt | ifStmt | whileStmt | forStmt | printStmt | returnStmt | block;
whileStmt   -> "while" "(" expression ")" statement;
forStmt     -> "for" "(" (varDecl  | exprStmt | ";" ) expression? ";" expression? ";" ")" statement;
ifStmt      -> "if" "(" expression ")" statement ( "else" statement )? ;
exprStmt    -> expression ";";
printStmt   -> "print" expression ";";
returnStmt  -> "return" expression ";" ;
block       -> "{" declaration "}";
expression  -> assignment;
assignment  -> ( call "." )? IDETIFIER "=" assignment | logic_or;
logic_or    -> logic_and ( "or" logic_and )*;
logic_and   -> equality ( "and" equality )*;
equality    -> comparison ( ( "!=" | "==" ) comparison )*;
comparison  -> term ( ( ">" | ">=" | "<" | "<=" ) term )*;
term        -> factor ( ( "-" | "+" ) factor )*;
factor      -> unary ( ( "/" | "*" ) unary )*;
unary       -> ( "!" | "-" ) unary | call;
call        -> primary ( "(" arguments? ")" | "." IDETIFIER )*;
arguments   -> expression ( "," expression )*;
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDETIFIER;
```


## Task
* [] break
* [] lambda expression

# Lox

## BNF
```
program     -> statement* EOF;
declaration -> varDecl | funDecl | statement;
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
assignment  -> IDETIFIER "=" assignment | logic_or;
logic_or    -> logic_and ( "or" logic_and )*;
logic_and   -> equality ( "and" equality )*;
equality    -> comparison ( ( "!=" | "==" ) comparison )*;
comparison  -> term ( ( ">" | ">=" | "<" | "<=" ) term )*;
term        -> factor ( ( "-" | "+" ) factor )*;
factor      -> unary ( ( "/" | "*" ) unary )*;
unary       -> ( "!" | "-" ) unary | call;
call        -> primary ( "(" arguments? ")" )*;
arguments   -> expression ( "," expression )*;
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDETIFIER;
```


## Task
* [] break

/*
      using a Java Development Kit 1.5 or called Java5 (JDK_VERSION= "1.5")
      for compilation compatibility with Java 5, and also an instance methods
      for parser (STATIC=false).
*/
options {
  JDK_VERSION = "1.5";
  STATIC = false;
}

PARSER_BEGIN(St4tic)
package st4tic;

import st4tic.syntaxtree.*;
import st4tic.visitor.*;

public class St4tic
{
  public static void main(String args[]) {
    try {
      Start start =  new St4tic(new java.io.StringReader(
      "require java lang.\n" +
      "def var = 13.\n" +
      "while var > 0  do\n" +
      "System:out:println( var ).\n" +
      "var = var - 1.\n" +
      "stop.\n"
      ) ).Start();
      start.accept( new DepthFirstVisitor () );
      System.out.println("Right! no errors founded! =)");
    } catch (Exception e) {
      System.out.println("Oops.");
      System.out.println(e.getMessage());
    }
  }
}

PARSER_END(St4tic)

/*
      for skipping a space between keyword, tab and new lines
      or returns, but last is for skipping comments, like in Java

            //comment here...

      in St4tic comment is :

            "Comment here...

*/
SKIP :
{
  " "
| "\t"
| "\n"
| "\r"
| <"\"" (~["\n","\r"])* ("\n"|"\r"|"\r\n")>
}

/*
      This can resume from first look, this a St4tic reserved keyword!
Right St4tic has only six reserved keywords.

      "require" keyword used for Java library importation like "import" in Java :

      require java lang.

      This import all "java.lang" class.

      "def" keyword this like "my" in Perl for
variable declaration, and can't declare any variable without using "def".

      def myVar = 1.
      def num13 = 13.

      "if" and "while" this is a classical if-condition and while-loop.

      if 1 > 0 do
            "do something …
      stop

      while 1 > 0 do
            "repeat in infinite loop …
      stop
*/
TOKEN : /*
KEYWORDS */
{
      < REQUERE: "require" >
|     < IF: "if" >
|     < WHILE: "while" >
|     < DO: "do" >
|     < STOP: "stop" >
|     < DEF : "def" >
}

/*
      Here, we can grouping symbols to "Math Operation Symbols" (+,-,*,/,%)
      and "Math Relational Symbols"
(>,<,==,>=,<=,!=).
*/
TOKEN : /* SYMBOLS */
{
      < DOT: "." >
|     < COLON: ":" >
|     < EQ: "==" >
|     < GT: ">"  >
|     < LT: "<"  >
|     < GE: ">=" >
|     < LE: "<=" >
|     < NE: "!=" >
|     < PLUS: "+">
|     < MINUS: "-" >
|     < MUL: "*" >
|     < DIV: "/" >
|     < MOD: "%" >
|     < ASSIGN: "=" >
}

/*
      Literals, maybe can said "value" or "data" (in St4tic) example:

      def myAge   = 24.
      def var     = 666.

      if 11 > 10 do
            …
      stop

      a values "24, 666, 11, 10" is checked or parsed as literals.
*/
TOKEN : /* LITERALS */
{
  < INTEGER_LITERAL: ["1"-"9"] (["0"-"9"])* | "0"   >
}

TOKEN : /* IDENTIFIERS */
{
  < IDENTIFIER: <LETTER> (<LETTER>|<DIGIT>)* >
|  < #LETTER: ["_","a"-"z","A"-"Z"] >
|  < #DIGIT: ["0"-"9"] >
}

/* GRAMMAR start here */


/*
      This is an enter point for St4tic parsing
without it, a parser can't started,
      for this rule we need mandatory to specifying
a "require" (if you notice "+", one or many)
      and after it a program instructions (notice
"*", no-one or many):
*/
void Start():{}
{
  (
    Require() "."
  )+

  (
    StatementExpression()
  )*
}

/*
      Here for packages importation can be one word after "require" or many like :

      require java .
      require java lang .
      ...
*/
void Require():{}
{
      "require"
      (
        < IDENTIFIER >
      )+
}

/* Simple Math Operations */
void MathExpression():{ }
{
  AdditiveExpression()
}

void AdditiveExpression():{}
{
  MultiplicativeExpression() ( ( "+" | "-" )
MultiplicativeExpression() )*
}

void MultiplicativeExpression():{}
{
  UnaryExpression() ( ( "*" | "/" | "%" ) UnaryExpression() )*
}

void UnaryExpression():{}
{
  "(" MathExpression() ")" | < INTEGER_LITERAL > | VariableName()
}

/* Start Simple Relational Test */
void RelationalExprssion():{}
{
      RelationalEqualityExpression()
}

void RelationalEqualityExpression():{}
{
      RelationalGreaterExpression()
      (
        (
           "==" | "!="
        )
        RelationalGreaterExpression()
      )*
}

void RelationalGreaterExpression():{}
{
      RelationalLessExpression()
      (
        (
           ">" | ">="
        )
         RelationalLessExpression()
      )*
}

void RelationalLessExpression():{}
{
      UnaryRelational()
      (
        (
           "<" | "<="
        )

        UnaryRelational()

      )*
}

void UnaryRelational():{}
{
         < INTEGER_LITERAL > |
VariableName()
}
/* End Simple Relational Test */

/*
      "if" expression is a classical test if true do something
      like in :
      -----------------------------------------------
            Java              |     VB
      -----------------------------------------------
      if( 1 > 0 )             |     If 1 > 0
Then
      {                       |           ...
            ...               |     End If
      }                       |
                              |
      Here is :
      if 1 > 0 do
            ...
      stop

      "stop" is end of if block
*/
void IfExpression():{}
{
      "if" RelationalExprssion() "do"
            (
              StatementExpression()
            ) *
      "stop"
}

/*
      "while" expression is a classical test while true repeat something
      like in :
      -----------------------------------------------
            Java              |     VB
      -----------------------------------------------
      while( 1 > 0 )          |     While 1 > 0
      {                       |           ...
            ...               |     End While
      }                       |
                              |
      Here is :
      while 1 > 0 do
            ...
      stop

      "stop" is end of while block
*/
void WhileExpression():{}
{
      "while" RelationalExprssion() "do"
            (
              StatementExpression()
            ) *
      "stop"
}

/*
      Variable declaration expression is a syntax of variables
      declaration in St4tic
      in Perl we declare variables by using keyword
"my"
      or VB by using "dim", St4tic use "def" for definition
      or define it.
      like def var = 1.
*/
void VariableDeclaration():{}
{
      "def" VariableName() "=" MathExpression() "."
}

void VariableAssign():
{
}
{
      VariableName() "=" MathExpression() "."
}

void VariableName():{}
{
      < IDENTIFIER >
}

void JavaStaticMethods():{}
{
      < IDENTIFIER > /* Class Name */
      (
        ":" < IDENTIFIER > /* Member or Method */
      )+

      "(" MathExpression() ( "," MathExpression() )* ")" "."

}

/*
      "statement expression" is program body oralgorithm can content
      a many variables declaration, variables assign, logical tests (if;while)
      or Java methods calling (remember in St4tic just public static methods).
*/
void StatementExpression():{}
{
  VariableDeclaration()
| LOOKAHEAD(2) VariableAssign()
| JavaStaticMethods()
| IfExpression()
| WhileExpression()
}

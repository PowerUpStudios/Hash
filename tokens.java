/*
      using a Java Development Kit 1.5 or called Java5 (JDK_VERSION= "1.5")
      for compilation compatibility with Java 5, and also an instance methods
      for parser (STATIC=false).
*/
options {
  JDK_VERSION = "1.5";
  STATIC = false;
}

PARSER_BEGIN(Hash)
package hash;

import hash.syntaxtree.*;
import hash.visitor.*;

public class Hash
{
  public static void main(String args[]) {
    try {
      Start start =  new Hash(new java.io.StringReader(
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

PARSER_END(Hash)


SKIP :
{
  " "
| "\t"
| "\n"
| "\r"
| <"\"" (~["\n","\r"])* ("\n"|"\r"|"\r\n")>
}


TOKEN : /*
KEYWORDS */
{
      < REQUERE: "$require" >
|     < IF: "$if" >
|     < WHILE: "$while" >
|     < DO: "$do" >
|     < STOP: "$end" >
|     < DEF : "$var" >
}


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


void Start():{}
{
  (
    Require() "."
  )+

  (
    StatementExpression()
  )*
}


void Require():{}
{
      "$require"
      (
        < IDENTIFIER >
      )+
}


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

void IfExpression():{}
{
      "$if" RelationalExprssion() "$do"
            (
              StatementExpression()
            ) *
      "$end"
}


void WhileExpression():{}
{
      "$while" RelationalExprssion() "$do"
            (
              StatementExpression()
            ) *
      "$end"
}


void VariableDeclaration():{}
{
      "$var" VariableName() "=" MathExpression() "."
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


void StatementExpression():{}
{
  VariableDeclaration()
| LOOKAHEAD(2) VariableAssign()
| JavaStaticMethods()
| IfExpression()
| WhileExpression()
}

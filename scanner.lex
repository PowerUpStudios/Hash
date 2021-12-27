// scanner.lex
/* Definitions */
%{
 #include <iostream>
 using namespace std;
 extern "C" int yylex();
 %}

/* Rules next */
%%
[0-9]+.[0-9]+ cout << "FLOAT: (" << yytext << ")" << endl;
[0-9]+        cout << "INT: (" << yytext << ")" << endl;
"+"           cout << "PLUS" << endl;
"-"           cout << "MINUS" << endl;
"*"           cout << "TIMES" << endl;
"/"           cout << "DIVIDED BY" << endl;
";"           cout << "SEMICOLON" << endl;
[\t\r\n\f]    ; /* ignore whitespace */

%%
/* Code */

int main() {
  yylex();
}

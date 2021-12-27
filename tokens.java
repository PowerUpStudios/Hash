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
      < REQUERE: "require" >
|     < IF: "if" >
|     < WHILE: "while" >
|     < DO: "do" >
|     < STOP: "stop" >
|     < DEF : "def" >
}

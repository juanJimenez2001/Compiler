package code;
import static code.Tokens.*;
%%
%class Lexemas
%type Tokens
L=[a-zA-Z_]+
C="\""~"\""
D=[0-9]+
espacio=[ \t\r\n]+
%{
    public String lexema;
%}
%%
int |
boolean |
break |
function |
input |
let |
print |
return |
string |
float |
string |
switch |
case |
if |
else |
while |
then |
for {lexema=yytext(); return Pal_Res;}
{espacio} {/*Ignore*/}
"//".* {/*Ignore*/}
"=" {return opAsignacion;}
"--" {return opAutodecremento;}
"+" {return suma;}
"!" {return negacion;}
">" {return mayor;}
"," {return coma;}
";" {return puntoComa;}
":" {return dosPuntos;}
"(" {return parentesisAbierto;}
")" {return parentesisCerrado;}
"{" {return llaveAbierta;}
"}" {return llaveCerrada;}
{C} {lexema=yytext(); return cadena;}
{L}({L}|{D})* {lexema=yytext(); return id;}
({D}+)|{D}+ {lexema=yytext(); return entero;}
. {return ERROR;}
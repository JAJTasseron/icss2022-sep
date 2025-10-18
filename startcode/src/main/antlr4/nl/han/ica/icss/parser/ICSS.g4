grammar ICSS;

//--- LEXER: ---

//IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//Mathematical and structural symbols
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';


//--- PARSER: ---
stylesheet: variableAssignment* stylerule* EOF;

stylerule: selector OPEN_BRACE (declaration|ifClause|elseClause|variableAssignment)+ CLOSE_BRACE;

selector: LOWER_IDENT | ID_IDENT | CLASS_IDENT;

ifClause: IF BOX_BRACKET_OPEN booleanExpression BOX_BRACKET_CLOSE OPEN_BRACE (declaration|ifClause|elseClause|variableAssignment)+ CLOSE_BRACE;
elseClause: ELSE OPEN_BRACE (declaration|ifClause|elseClause|variableAssignment)+ CLOSE_BRACE;

variableAssignment: variableReference ASSIGNMENT_OPERATOR value SEMICOLON;
declaration: property COLON (expression|variableReference|value) SEMICOLON;
expression: expressionable
            |expression MUL expression
            |expression (PLUS|MIN) expression;
expressionable: variableReference | PIXELSIZE | PERCENTAGE | SCALAR;
booleanExpression: variableReference|TRUE|FALSE;
//operator: (PLUS|MUL|MIN);

variableReference: CAPITAL_IDENT;
property: LOWER_IDENT;
value: TRUE | FALSE | PIXELSIZE | PERCENTAGE | SCALAR | COLOR;


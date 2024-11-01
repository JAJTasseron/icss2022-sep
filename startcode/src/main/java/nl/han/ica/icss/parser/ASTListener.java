package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	// Stylesheet
	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet= new Stylesheet();
		currentContainer.push(stylesheet);
	}
	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet sheet = (Stylesheet) currentContainer.pop();
		ast.root = sheet;
	}

	// VariableAssignment
	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment assignment = new VariableAssignment();
		currentContainer.push(assignment);
	}
	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment assignment = (VariableAssignment) currentContainer.pop();
		currentContainer.peek().addChild(assignment);
	}

	// Stylerule
	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}
	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule rule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(rule);
	}

	// Selector
	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		if(ctx.getText().startsWith(".")){
			Selector selector = new ClassSelector(ctx.getText());
			currentContainer.push(selector);
		} else if (ctx.getText().startsWith("#")) {
			Selector selector = new IdSelector(ctx.getText());
			currentContainer.push(selector);
		} else {
			Selector selector = new TagSelector(ctx.getText());
			currentContainer.push(selector);
		}
	}
	@Override
	public void exitSelector(ICSSParser.SelectorContext ctx) {
		Selector selector = (Selector) currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	// IfClause TODO: Fix deze zodat hij geen ASTNode die NULL is teruggeeft en maak daarna nog de expression
	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause clause = new IfClause();
		currentContainer.push(clause);
	}
	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause clause = (IfClause) currentContainer.pop();
		currentContainer.peek().addChild(clause);
	}

	// ElseClause
	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause clause = new ElseClause();
		currentContainer.push(clause);
	}
	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause clause = (ElseClause) currentContainer.pop();
		currentContainer.peek().addChild(clause);
	}

	// Decleration
	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		currentContainer.push(declaration);
	}
	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	// Property
	@Override
	public void enterProperty(ICSSParser.PropertyContext ctx) {
		PropertyName propertyName = new PropertyName(ctx.getText());
		currentContainer.push(propertyName);
	}
	@Override
	public void exitProperty(ICSSParser.PropertyContext ctx) {
		PropertyName propertyName = (PropertyName) currentContainer.pop();
		currentContainer.peek().addChild(propertyName);
	}

	// Expression
	@Override
	public void enterExpression(ICSSParser.ExpressionContext ctx) {
		if(ctx.getChildCount()==3){
			if (ctx.getChild(1).getText().equals("+")){
				Operation operation = new AddOperation();
				currentContainer.push(operation);
			} else if (ctx.getChild(1).getText().equals("*")) {
				Operation operation = new MultiplyOperation();
				currentContainer.push(operation);
			} else if (ctx.getChild(1).getText().equals("-")) {
				Operation operation = new SubtractOperation();
				currentContainer.push(operation);
			}
		}
	}
	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		if (ctx.getChildCount()==3){
			Operation operation = (Operation) currentContainer.pop();
			currentContainer.peek().addChild(operation);
		}
	}

	// Value
	@Override
	public void enterValue(ICSSParser.ValueContext ctx) {
		if(ctx.getText().equals("TRUE")|ctx.getText().equals("FALSE")){
			Literal literal = new BoolLiteral(ctx.getText());
			currentContainer.push(literal);
		} else if (ctx.getText().startsWith("#")) {
			Literal literal = new ColorLiteral(ctx.getText());
			currentContainer.push(literal);
		} else if (ctx.getText().endsWith("%")) {
			Literal literal = new PercentageLiteral(ctx.getText());
			currentContainer.push(literal);
		} else if (ctx.getText().endsWith("px")) {
			Literal literal = new PixelLiteral(ctx.getText());
			currentContainer.push(literal);
		} else {
			Literal literal = new ScalarLiteral(ctx.getText());
			currentContainer.push(literal);
		}
	}
	@Override
	public void exitValue(ICSSParser.ValueContext ctx) {
		Literal literal = (Literal) currentContainer.pop();
		currentContainer.peek().addChild(literal);
	}

	// VariableReference
	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		VariableReference value = new VariableReference(ctx.getText());
		currentContainer.push(value);
	}
	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		VariableReference value = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(value);
	}

	//BooleanExpression
	@Override
	public void enterBooleanExpression(ICSSParser.BooleanExpressionContext ctx) {
		if (ctx.getChild(0).getChildCount()==0){
			BoolLiteral bool = new BoolLiteral(ctx.getText());
			currentContainer.push(bool);
		}
	}
	@Override
	public void exitBooleanExpression(ICSSParser.BooleanExpressionContext ctx) {
		if (ctx.getChild(0).getChildCount()==0){
			BoolLiteral bool = (BoolLiteral) currentContainer.pop();
			currentContainer.peek().addChild(bool);
		}
	}

	// Expressionable
	@Override
	public void enterExpressionable(ICSSParser.ExpressionableContext ctx) {
		if (ctx.getChild(0).getChildCount()==0){
			if (ctx.getText().endsWith("%")) {
				Literal literal = new PercentageLiteral(ctx.getText());
				currentContainer.push(literal);
			} else if (ctx.getText().endsWith("px")) {
				Literal literal = new PixelLiteral(ctx.getText());
				currentContainer.push(literal);
			} else {
				Literal literal = new ScalarLiteral(ctx.getText());
				currentContainer.push(literal);
			}
		}
	}
	@Override
	public void exitExpressionable(ICSSParser.ExpressionableContext ctx) {
		if (ctx.getChild(0).getChildCount()==0){
			Literal literal = (Literal) currentContainer.pop();
			currentContainer.peek().addChild(literal);
		}
	}
}
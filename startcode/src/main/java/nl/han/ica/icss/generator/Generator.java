package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

	public String generate(AST ast) {
		return generateStylesheet(ast.root);
	}

	// Onderdelen
	private String generateStylesheet(Stylesheet root) {
		if(root.getChildren().isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		for (ASTNode child : root.getChildren()) {
			if (child instanceof Stylerule) {
				sb.append(generateStylerule((Stylerule) child));
			}
		}
		return sb.toString();
	}

	private String generateStylerule(Stylerule stylerule) {
		String result = stylerule.selectors.get(0) + " {\n";
		result += "\t" + generateDeclaration(stylerule.body.get(0));
		result += "\n}\n";
		return result;
	}

	private String generateDeclaration(ASTNode astNode) {
		Declaration	declaration = (Declaration) astNode;
		String result = declaration.property.name + ": ";
		if(declaration.expression instanceof ColorLiteral) {
			ColorLiteral colorLiteral = (ColorLiteral) declaration.expression;
			result += colorLiteral.value;
		} else if (declaration.expression instanceof ScalarLiteral) {
			ScalarLiteral scalarLiteral = (ScalarLiteral) declaration.expression;
			result += scalarLiteral.value;
		} else if (declaration.expression instanceof PercentageLiteral) {
			PercentageLiteral percentageLiteral = (PercentageLiteral) declaration.expression;
			result += percentageLiteral.value;
		} else if (declaration.expression instanceof PixelLiteral) {
			PixelLiteral pixelLiteral = (PixelLiteral) declaration.expression;
			result += pixelLiteral.value;
		} else if (declaration.expression instanceof BoolLiteral) {
			BoolLiteral boolLiteral = (BoolLiteral) declaration.expression;
			result += boolLiteral.value;
		}

		return result;
	}
}

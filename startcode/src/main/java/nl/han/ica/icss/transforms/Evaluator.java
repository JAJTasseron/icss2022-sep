package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

import static nl.han.ica.icss.ast.types.ExpressionType.*;
import static nl.han.ica.icss.ast.types.ExpressionType.SCALAR;

public class Evaluator implements Transform {

    // TODO: Vul variabelen in en berekeningen uitwerken

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        //variableValues = new HANLinkedList<>(); TODO: Check of dit hier of in de apply moet (dit stond hier oorspronkelijk in)
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);

        /* TODO: Evalueer if/else expressies. Schrijf een transformatie in Evaluator die alle IfClauses uit de AST verwijdert.
            Wanneer de conditie van de IfClause TRUE is wordt deze vervangen door de body van het if-statement.
            Als de conditie FALSE is dan vervang je de IfClause door de body van de ElseClause.
            Als er geen ElseClause is bij een negatieve conditie dan verwijder je de IfClause volledig uit de AST. */
    }

    // Onderdelen
    private void applyStylesheet(Stylesheet node) {
        for(ASTNode child : node.getChildren()) {
            if (child instanceof Stylerule) {
                applyStylerule((Stylerule) child);
            }
        }
    }

    private void applyStylerule(Stylerule node) {
         for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            }
         }
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evalExpression(node.expression);
    }

    private Expression evalExpression(Expression expression) {
        /* Schrijf een transformatie in Evaluator die alle Expression knopen in de AST
            door een Literal knoop met de berekende waarde vervangt.*/
        int value = calculateEquation(expression, expression.getNodeLabel(), expression.getChildren().get(0),expression.getChildren().get(expression.getChildren().size()-1));
        ExpressionType type = findExpressionTypeOfEquation(expression);
        if (type == PIXEL){
            return new PixelLiteral(value);
        } else if (type == PERCENTAGE) {
            return new PercentageLiteral(value);
        } else if (type == SCALAR) {
            return new ScalarLiteral(value);
        }
        return expression;
    }

    public int calculateEquation(ASTNode node, String nodeLabel, ASTNode lhs, ASTNode rhs){
        int leftValue = 0;
        int rightValue = 0;

        if(lhs.getChildren().size()>1){
            leftValue = calculateEquation(node,lhs.getNodeLabel(),lhs.getChildren().get(0),lhs.getChildren().get(lhs.getChildren().size()-1));
        } else {
            leftValue = findValueOfLiteral(lhs);
        }
        if(rhs.getChildren().size()>1){
            rightValue = calculateEquation(node,rhs.getNodeLabel(),rhs.getChildren().get(0),rhs.getChildren().get(rhs.getChildren().size()-1));
        } else {
            rightValue = findValueOfLiteral(rhs);
        }

        switch (nodeLabel) {
            case "Add":
                return leftValue + rightValue;
            case "Subtract":
                return leftValue - rightValue;
            case "Multiply":
                return leftValue * rightValue;
        }
        return 0;
    }

    private ExpressionType findExpressionTypeOfEquation(ASTNode node){
        ExpressionType type = SCALAR;
        if(node.getChildren().get(0).getChildren().size()>1){
            type = findExpressionTypeOfEquation(node.getChildren().get(0));
        }
        if(node.getChildren().get(0).getChildren().size()>1
                && findExpressionTypeOfEquation(node.getChildren().get(node.getChildren().size()-1)) != SCALAR){
                type = findExpressionTypeOfEquation(node.getChildren().get(node.getChildren().size()-1));
        }
        for(ASTNode child : node.getChildren()){
            if(child instanceof PixelLiteral){
                type = PIXEL;
            } else if(child instanceof PercentageLiteral){
                type = PERCENTAGE;
            }
        }
        return type;
    }

    private int findValueOfLiteral(ASTNode node){
        if (node instanceof PixelLiteral){
            return ((PixelLiteral) node).value;
        } else if (node instanceof PercentageLiteral) {
            return ((PercentageLiteral) node).value;
        } else if (node instanceof  ScalarLiteral) {
            return ((ScalarLiteral) node).value;
        }
        return 0;
    }

//    private ExpressionType findExpressionTypeOfNode(ASTNode node){
//        if(node instanceof VariableReference){
//            String variableName = ((VariableReference) node).name;
//            int scopeOfVariable = findScopeOfVariable(node, variableName);
//            if (scopeOfVariable == -1){ return null; }
//            return variableValues.get(scopeOfVariable).get(variableName);
//        } else if (node instanceof PixelLiteral){
//            return PIXEL;
//        } else if (node instanceof PercentageLiteral){
//            return PERCENTAGE;
//        } else if (node instanceof ScalarLiteral){
//            return SCALAR;
//        }
//        return null;
//    }
//
//    private int findScopeOfVariable(ASTNode node, String variableName){
//        for(int i = 0; i < variableValues.getSize(); i++){
//            HashMap<String, ExpressionType> currentHashmap = variableValues.get(i);
//            if(currentHashmap.containsKey(variableName)){
//                return i;
//            }
//        }
//    }
}



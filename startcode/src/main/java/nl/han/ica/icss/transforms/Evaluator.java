package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;

import static nl.han.ica.icss.ast.types.ExpressionType.*;
import static nl.han.ica.icss.ast.types.ExpressionType.SCALAR;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);
    }

    // Onderdelen
    private void applyStylesheet(Stylesheet node) {
        variableValues.addFirst(new HashMap<>());
        for(ASTNode child : node.getChildren()) {
            if(child instanceof VariableAssignment) {
                applyVariableAssignment((VariableAssignment) child);
            }
            if (child instanceof Stylerule) {
                applyStylerule((Stylerule) child);
            }
        }
    }

    private void applyVariableAssignment(VariableAssignment node) {
        variableValues.getFirst().put(node.name.name, (Literal) node.expression);
    }

    private void applyStylerule(Stylerule node) {
        ArrayList<ASTNode> originalBody = node.body;
        ArrayList<ASTNode> newBody = new ArrayList<>();

        applyThroughIteratedStyleruleNodes(originalBody, newBody);

        node.body = newBody;
    }

    private void applyThroughIteratedStyleruleNodes(ArrayList<ASTNode> originalBody, ArrayList<ASTNode> newBody) {
        for (ASTNode child : originalBody) {
            if (child instanceof VariableAssignment) {
                applyVariableAssignment((VariableAssignment) child);
            }
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
                newBody.add(child);
            }
            if (child instanceof IfClause) {
                applyIfClause((IfClause) child, newBody);
            }
        }
    }

    private void applyIfClause(IfClause ifClause, ArrayList<ASTNode> newBody) {
        boolean conditionTrue = ifClauseBooleanIsTrue(ifClause);

        ArrayList<ASTNode> chosenBody = new ArrayList<>();
        if (conditionTrue) {
            chosenBody.addAll(ifClause.body);
        } else if (ifClause.elseClause != null) {
            chosenBody.addAll(ifClause.elseClause.body);
        }

        applyThroughIteratedStyleruleNodes(chosenBody, newBody);
    }

    private void applyDeclaration(Declaration node) {
        if (node.expression != null) {
            if (node.expression instanceof VariableReference) {
                node.expression = variableValues.getFirst().get(((VariableReference) node.expression).name);
            }
            if (!node.expression.getChildren().isEmpty()) {
                node.expression = evalExpression(node.expression);
            }
        }
    }

    private boolean ifClauseBooleanIsTrue(IfClause node) {
        ASTNode condition = node.getConditionalExpression();
        if (condition instanceof BoolLiteral) return ((BoolLiteral) condition).value;
        else if (condition instanceof VariableReference) {
            Literal literal = variableValues.getFirst().get(((VariableReference) condition).name);
            if (literal instanceof BoolLiteral) return ((BoolLiteral) literal).value;
        }
        return false;
    }

    private Expression evalExpression(Expression expression) {
        /* Schrijf een transformatie in Evaluator die alle Expression knopen in de AST
            door een Literal knoop met de berekende waarde vervangt.*/
        int value = calculateEquation(expression, expression.getNodeLabel(), expression.getChildren().get(0), expression.getChildren().get(expression.getChildren().size()-1));
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
            default:
                return 0;
        }
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
        if (node instanceof VariableReference){
            String variableName = ((VariableReference) node).name;
            return findValueOfLiteral(variableValues.getFirst().get(variableName));
        }

        if (node instanceof PixelLiteral){
            return ((PixelLiteral) node).value;
        } else if (node instanceof PercentageLiteral) {
            return ((PercentageLiteral) node).value;
        } else if (node instanceof  ScalarLiteral) {
            return ((ScalarLiteral) node).value;
        }
        return 0;
    }
}



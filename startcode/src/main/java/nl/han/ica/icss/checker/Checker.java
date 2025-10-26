package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

import static nl.han.ica.icss.ast.types.ExpressionType.*;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        variableTypes.addFirst(new HashMap<>());
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet node) {
        for(ASTNode child : node.getChildren()){
            if(child instanceof VariableAssignment) checkVariableAssignment((VariableAssignment) child);
            if(child instanceof Stylerule)  checkStylerule((Stylerule) child);
        }
    }

    private void checkVariableAssignment(VariableAssignment node) {
        if(node.expression instanceof BoolLiteral) variableTypes.get(0).put(node.name.name, BOOL);
        if(node.expression instanceof ColorLiteral) variableTypes.get(0).put(node.name.name, COLOR);
        if(node.expression instanceof PercentageLiteral) variableTypes.get(0).put(node.name.name, PERCENTAGE);
        if(node.expression instanceof PixelLiteral) variableTypes.get(0).put(node.name.name, PIXEL);
        if(node.expression instanceof ScalarLiteral) variableTypes.get(0).put(node.name.name, SCALAR);
    }

    private void checkStylerule(Stylerule node) {
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode child : node.getChildren()){
            if (child instanceof Declaration) checkDeclaration((Declaration) child);
            else if (child instanceof IfClause) checkIfClause((IfClause) child);
            else if (child instanceof VariableAssignment) checkVariableAssignment((VariableAssignment) child);
        }
        variableTypes.delete(0);
    }

    private void checkIfClause(IfClause node) {
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode child : node.getChildren()){
            if (child instanceof IfClause){
                checkIfClause((IfClause) child);
            } else if (child instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) child);
            }
        }
        /* Controleer of de conditie bij een if-statement van het type boolean is (zowel bij een variabele-referentie als een boolean literal) */
        if (node.conditionalExpression instanceof VariableReference) {
            String variableName = ((VariableReference) node.conditionalExpression).name;
            if (variableTypes.get(findScopeOfVariable(node, variableName)).get(variableName) != BOOL){
                node.setError("Condition of an if-statement needs to be a boolean.");
            }
        } else if(!(node.conditionalExpression instanceof BoolLiteral)){
            node.setError("Condition of an if-statement needs to be a boolean.");
        }
        variableTypes.delete(0);
    }

    public void checkDeclaration(Declaration node){
        for (ASTNode child : node.getChildren()){
            if(child instanceof VariableReference
                    && !variableReferenceIsSet((VariableReference) child)) {
                    return;
            }
        }

        if(node.expression.getChildren().size()>1){
            checkOperationIntegrity(node);
        } else {
            checkDeclarationIntegrity(node);
        }
    }

    /* Controleer of de operanden van de operaties plus en min van gelijk type zijn. */
    private void checkOperationIntegrity(Declaration node){
        Expression currentExpression = node.expression;
        ASTNode lhs = currentExpression.getChildren().get(0);
        ASTNode rhs = currentExpression.getChildren().get(currentExpression.getChildren().size()-1);

        if (usesColorLiteralInExpression(currentExpression)) {
            node.setError("Can't have a color in an equation.");
        }

        checkLeftVariableSameTypeAsRightVariable(node, currentExpression.getNodeLabel(), lhs, rhs);
    }

    /* Controleer of bij declaraties het type van de value klopt met de property.  */
    private void checkDeclarationIntegrity(Declaration node) {
        String property = node.property.name;
        if (property.equals("width") || property.equals("height")) checkExpectedType(node, PIXEL, PixelLiteral.class, "pixel size");
        else if (property.contains("color")) checkExpectedType(node, COLOR, ColorLiteral.class, "color");
    }

    private void checkExpectedType(Declaration node, ExpressionType expectedType,
                                   Class<?> expectedLiteralClass, String typeName) {
        if (node.expression instanceof VariableReference) {
            String variableName = ((VariableReference) node.expression).name;
            int scope = findScopeOfVariable(node, variableName);
            if (scope == -1) return;

            ExpressionType actualType = variableTypes.get(scope).get(variableName);
            if (actualType != expectedType) node.setError("Variable is not a " + typeName + " where it is expected.");
        }
        else if (!expectedLiteralClass.isInstance(node.expression)) node.setError("Property should be a " + typeName + ".");
    }

    /* Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer). */
    public boolean usesColorLiteralInExpression(Expression expression){
        for(ASTNode astNode : expression.getChildren()){
            if (findExpressionTypeOfNode(astNode) == COLOR) return true;
        }
        return false;
    }

    public void checkLeftVariableSameTypeAsRightVariable(ASTNode node, String nodeLabel, ASTNode lhs, ASTNode rhs){
        if(lhs.getChildren().size()>1){
            checkLeftVariableSameTypeAsRightVariable(node,lhs.getNodeLabel(),lhs.getChildren().get(0),lhs.getChildren().get(lhs.getChildren().size()-1));
        }
        if(rhs.getChildren().size()>1){
            checkLeftVariableSameTypeAsRightVariable(node,rhs.getNodeLabel(),rhs.getChildren().get(0),rhs.getChildren().get(rhs.getChildren().size()-1));
        }
        ExpressionType lhsType = findExpressionTypeOfNode(findCorrectEquationChild(lhs));
        ExpressionType rhsType = findExpressionTypeOfNode(findCorrectEquationChild(rhs));
        if ((nodeLabel.equals("Add") || nodeLabel.equals("Subtract"))&&(lhsType != rhsType)) {
            node.setError("Variable is not of the same type as other variables in this operation.");
        }
        if ((nodeLabel.equals("Multiply"))&&(lhsType != SCALAR && rhsType != SCALAR)){
            node.setError("Multiplication requires at least one scalar.");
        }
    }

    private ASTNode findCorrectEquationChild(ASTNode node){
        while(node.getChildren().size()>1){
            if (findExpressionTypeOfNode(node.getChildren().get(0))==SCALAR){
                node = node.getChildren().get(node.getChildren().size()-1);
            } else {
                node = node.getChildren().get(0);
            }
        }
        return node;
    }

    private ExpressionType findExpressionTypeOfNode(ASTNode node){
        if(node instanceof VariableReference){
            String variableName = ((VariableReference) node).name;
            int scopeOfVariable = findScopeOfVariable(node, variableName);
            if (scopeOfVariable == -1){ return null; }
            return variableTypes.get(scopeOfVariable).get(variableName);
        }
        else if (node instanceof PixelLiteral) return PIXEL;
        else if (node instanceof PercentageLiteral) return PERCENTAGE;
        else if (node instanceof ScalarLiteral) return SCALAR;
        return null;
    }

    /* MUST Controleer of variabelen enkel binnen hun scope gebruikt worden */
    private int findScopeOfVariable(ASTNode node, String variableName){
        for(int i = 0; i < variableTypes.getSize(); i++){
            HashMap<String, ExpressionType> currentHashmap = variableTypes.get(i);
            if(currentHashmap.containsKey(variableName)) return i;
        }
        node.setError("Can not use a variable outside of the current scope."); // Dit zou niet moeten gebeuren aangezien de variabele verwijderd wordt als die buiten de scope valt
        return -1;
    }

    /* Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn. */
    public boolean variableReferenceIsSet(VariableReference node){
        boolean variableIsSet = false;

        for(int i = 0; i < variableTypes.getSize(); i++){
            HashMap<String, ExpressionType> currentHashmap = variableTypes.get(i);
            for (String key : currentHashmap.keySet()){
                if (node.name.equals(key)) {
                    variableIsSet = true;
                    break;
                }
            }
        }

        if (!variableIsSet) node.setError("Variable is not declared or out of scope.");
        return variableIsSet;
    }
}

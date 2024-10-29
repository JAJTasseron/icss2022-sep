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

        // TODO: Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer).

        /* TODO: Controleer of de conditie bij een if-statement van het type boolean is
            (zowel bij een variabele-referentie als een boolean literal) */

        // TODO: MUST Controleer of variabelen enkel binnen hun scope gebruikt worden
    }

    private void checkStylesheet(Stylesheet node) {
        for(ASTNode child : node.getChildren()){
            if(child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
            if(child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
    }

    private void checkVariableAssignment(VariableAssignment node) {
        if(node.expression instanceof BoolLiteral){
            variableTypes.get(0).put(node.name.name, BOOL);
        }
        if(node.expression instanceof ColorLiteral){
            variableTypes.get(0).put(node.name.name, COLOR);
        }
        if(node.expression instanceof PercentageLiteral){
            variableTypes.get(0).put(node.name.name, PERCENTAGE);
        }
        if(node.expression instanceof PixelLiteral){
            variableTypes.get(0).put(node.name.name, PIXEL);
        }
        if(node.expression instanceof ScalarLiteral){
            variableTypes.get(0).put(node.name.name, SCALAR);
        }
    }

    private void checkStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()){
            if(child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
    }

    public void checkDeclaration(Declaration node){
        for (ASTNode child : node.getChildren()){
            if(child instanceof VariableReference) {
                checkVariableReference((VariableReference) child);
            }
        }

        /* Controleer of de operanden van de operaties plus en min van gelijk type zijn.
            Je mag geen pixels bij percentages optellen bijvoorbeeld.
            Controleer dat bij vermenigvuldigen minimaal een operand een scalaire waarde is.
            Zo mag 20% * 3 en 4 * 5 wel, maar mag 2px * 3px niet. */
        if(node.expression.getChildren().size()>1){
            Expression currentExpression = node.expression;
            ASTNode lhs = currentExpression.getChildren().getFirst();
            ASTNode rhs = currentExpression.getChildren().getLast();

            if (!checkLeftVariableSameTypeAsRightVariable(currentExpression.getNodeLabel(), lhs, rhs)){
                node.setError("Variable is not of the same type as other variables in this operation.");
            }

        } else {
        /* Controleer of bij declaraties het type van de value klopt met de property.
            Declaraties zoals width: #ff0000 of color: 12px zijn natuurlijk onzin. */
            if (node.property.name.equals("width") | node.property.name.equals("height")){
                if(node.expression instanceof VariableReference){
                    if(variableTypes.get(0).get(((VariableReference) node.expression).name) != PIXEL){
                        node.setError("Variable is not a pixel size where it is expected.");
                    }
                } else if (!(node.expression instanceof PixelLiteral)){
                    node.setError("Property should be a pixel size.");
                }
            }
            if (node.property.name.contains("color")){
                if(node.expression instanceof VariableReference){
                    if(variableTypes.get(0).get(((VariableReference) node.expression).name) != COLOR){
                        node.setError("Variable is not a color where it is expected.");
                    }
                } else if (!(node.expression instanceof ColorLiteral)){
                    node.setError("Property should be a color.");
                }
            }
        }
    }

    public boolean checkLeftVariableSameTypeAsRightVariable(String nodeLabel, ASTNode lhs, ASTNode rhs){
        if(rhs.getChildren().size()>1){
            checkLeftVariableSameTypeAsRightVariable(
                    rhs.getNodeLabel(),
                    rhs.getChildren().getFirst(),
                    rhs.getChildren().getLast()
            );
        } else {
            if (nodeLabel.equals("Add") | nodeLabel.equals("Subtract")){
                // Als het + of - is, check of de twee children dezelfde type hebben
                ExpressionType lhsType = SCALAR;
                if(lhs instanceof VariableReference){
                    lhsType = variableTypes.get(0).get(((VariableReference) lhs).name);
                } else if (lhs instanceof PixelLiteral){
                    lhsType = PIXEL;
                } else if (lhs instanceof PercentageLiteral) {
                    lhsType = PERCENTAGE;
                }
                if(rhs instanceof VariableReference){
                    if(lhsType != variableTypes.get(0).get(((VariableReference) rhs).name)){
                        return false;
                    }
                } else if (rhs instanceof PixelLiteral){
                    if(lhsType != PIXEL){
                        return false;
                    }
                } else if (rhs instanceof PercentageLiteral) {
                    if(lhsType != PERCENTAGE){
                        return false;
                    }
                } else if (rhs instanceof ScalarLiteral) {
                    if(lhsType != SCALAR){
                        return false;
                    }
                }
            }
            if (nodeLabel.equals("Multiply")){
                // Als het * is, check of het twee verschillende typen zijn met 1 scalar
                ExpressionType lhsType = SCALAR;
                if(lhs instanceof VariableReference){
                    lhsType = variableTypes.get(0).get(((VariableReference) lhs).name);
                } else if (lhs instanceof PixelLiteral){
                    lhsType = PIXEL;
                } else if (lhs instanceof PercentageLiteral) {
                    lhsType = PERCENTAGE;
                }
                if (lhsType == SCALAR){
                    return true;
                } else {
                    if(rhs instanceof VariableReference){
                        return variableTypes.get(0).get(((VariableReference) rhs).name) == SCALAR;
                    } else if (rhs instanceof PixelLiteral){
                        return lhsType == PIXEL;
                    } else if (rhs instanceof PercentageLiteral) {
                        return lhsType == PERCENTAGE;
                    }
                }
            }
        }
        return true;
    }

    public void checkVariableReference(VariableReference node){
        /* Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn. */
        boolean variableIsSet = false;
        HashMap<String, ExpressionType> variableMap = variableTypes.get(0);

        for (String key : variableMap.keySet()){
            if (node.name.equals(key)) {
                variableIsSet = true;
                break;
            }
        }

        if (!variableIsSet){
            node.setError("Variable is not declared.");
        }
    }
}

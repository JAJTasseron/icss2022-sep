package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;

import java.util.HashMap;

public class Evaluator implements Transform {

    // TODO: Vul variabelen in en berekeningen uitwerken

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        //variableValues = new HANLinkedList<>(); TODO: Check of dit hier of in de apply moet (dit stond hier oorspronkelijk in)
    }

    @Override    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);
    }

    // Onderdelen
    private void applyStylesheet(Stylesheet node) {
         applyStylerule((Stylerule) node.getChildren().get(0));
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

    // ??? TODO: Wat moet ik evalueren aan deze onderdelen?
    private Expression evalExpression(Expression expression) {
        return null;
    }
}



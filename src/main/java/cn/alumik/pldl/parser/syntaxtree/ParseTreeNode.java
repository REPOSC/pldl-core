package cn.alumik.pldl.parser.syntaxtree;

import cn.alumik.pldl.symbol.Symbol;
import cn.alumik.pldl.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {

    private final String id = IdGenerator.next();

    private final Symbol symbol;

    private final List<ParseTreeNode> children = new ArrayList<>();

    public ParseTreeNode(Symbol symbol) {
        this.symbol = symbol;
    }

    String getId() {
        return id;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    List<ParseTreeNode> getChildren() {
        return children;
    }

    public void addChildren(ParseTreeNode node) {
        children.add(node);
    }
}

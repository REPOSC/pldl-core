package cn.alumik.pldl.symbol;

public class NonTerminalSymbol extends Symbol {

    public NonTerminalSymbol(AbstractNonTerminalSymbol abstractNonTerminalSymbol) {
        setAbstractSymbol(abstractNonTerminalSymbol);
        addProperty("value", abstractNonTerminalSymbol.getName());
    }
}

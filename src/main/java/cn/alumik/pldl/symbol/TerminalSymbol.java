package cn.alumik.pldl.symbol;

public class TerminalSymbol extends Symbol {

    public TerminalSymbol(AbstractTerminalSymbol abstractTerminalSymbol) {
        setAbstractSymbol(abstractTerminalSymbol);
        addProperty("value", abstractTerminalSymbol.getName());
    }
}

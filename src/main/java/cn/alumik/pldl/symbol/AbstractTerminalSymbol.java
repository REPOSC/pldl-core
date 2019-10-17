package cn.alumik.pldl.symbol;

public class AbstractTerminalSymbol extends AbstractSymbol {

    public AbstractTerminalSymbol(String name) {
        setName(name);
    }

    static AbstractTerminalSymbol Null() {
        return new AbstractTerminalSymbol("null");
    }

    public static AbstractTerminalSymbol End() {
        return new AbstractTerminalSymbol("$");
    }

    @Override
    public int getType() {
        return TERMINAL_SYMBOL;
    }
}

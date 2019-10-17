package cn.alumik.pldl.symbol;

import cn.alumik.pldl.parser.Production;

import java.util.HashSet;
import java.util.Set;

public class AbstractNonTerminalSymbol extends AbstractSymbol {

    private boolean nullable;

    private Set<AbstractTerminalSymbol> firstSet;

    private final Set<Production> productions = new HashSet<>();

    public AbstractNonTerminalSymbol(String name) {
        setName(name);
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Set<AbstractTerminalSymbol> getFirstSet() {
        return firstSet;
    }

    public void setFirstSet(Set<AbstractTerminalSymbol> firstSet) {
        this.firstSet = firstSet;
    }

    public Set<Production> getProductions() {
        return productions;
    }

    @Override
    public int getType() {
        return NON_TERMINAL_SYMBOL;
    }
}

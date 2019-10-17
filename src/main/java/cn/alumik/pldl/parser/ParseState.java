package cn.alumik.pldl.parser;

import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.symbol.AbstractNonTerminalSymbol;
import cn.alumik.pldl.symbol.AbstractSymbol;
import cn.alumik.pldl.symbol.AbstractTerminalSymbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ParseState {

    private final Set<Item> items;

    private final ContextFreeGrammar grammar;

    public ParseState(ContextFreeGrammar grammar) {
        this.grammar = grammar;
        items = new HashSet<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Set<Item> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParseState) {
            ParseState parseState = (ParseState) obj;
            return items.size() == parseState.items.size() && items.containsAll(parseState.items);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Item item : items) {
            hash ^= item.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("状态:\n");
        for (Item item : items) {
            stringBuilder.append(item);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void makeClosure() {
        List<Item> itemList = new ArrayList<>(items);
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (item.isNotEnded()) {
                AbstractSymbol abstractSymbol = item.getNextSymbol();
                if (abstractSymbol.getType() == AbstractSymbol.NON_TERMINAL_SYMBOL) {
                    List<AbstractSymbol> lookAheadSymbols = new ArrayList<>();
                    for (int j = item.getDot() + 1; j < item.getProduction().to().size(); j++) {
                        lookAheadSymbols.add(item.getProduction().to().get(j));
                    }
                    lookAheadSymbols.add(item.getLookAhead());
                    Set<AbstractTerminalSymbol> headList = getHeadSet(lookAheadSymbols);
                    for (Production production : ((AbstractNonTerminalSymbol) abstractSymbol).getProductions()) {
                        for (AbstractTerminalSymbol lookAheadSymbol : headList) {
                            Item newItem = new Item(production, lookAheadSymbol);
                            if (!items.contains(newItem)) {
                                items.add(newItem);
                                itemList.add(newItem);
                            }
                        }
                    }
                }
            }
        }
    }

    private Set<AbstractTerminalSymbol> getHeadSet(List<AbstractSymbol> abstractSymbols) {
        Set<AbstractTerminalSymbol> headSet = new HashSet<>();
        for (AbstractSymbol abstractSymbol : abstractSymbols) {
            if (abstractSymbol.getType() == AbstractSymbol.NON_TERMINAL_SYMBOL) {
                headSet.addAll(((AbstractNonTerminalSymbol) abstractSymbol).getFirstSet());
                if (!((AbstractNonTerminalSymbol) abstractSymbol).isNullable()) {
                    break;
                }
            } else {
                headSet.add((AbstractTerminalSymbol) abstractSymbol);
                if (!abstractSymbol.getName().equals("null")) {
                    break;
                }
            }
        }
        try {
            headSet.remove(grammar.getSymbolPool().getTerminalSymbol("null"));
        } catch (AnalysisException e) {
            e.printStackTrace();
        }
        return headSet;
    }
}

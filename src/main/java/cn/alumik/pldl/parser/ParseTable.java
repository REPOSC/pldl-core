package cn.alumik.pldl.parser;

import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.symbol.AbstractSymbol;

import java.util.*;

public class ParseTable {

    private final Map<Integer, Map<AbstractSymbol, Transition>> tableMap = new HashMap<>();

    private int acceptState;

    private final ContextFreeGrammar grammar;

    public ParseTable(ContextFreeGrammar grammar) {
        this.grammar = grammar;
    }

    public Map<Integer, Map<AbstractSymbol, Transition>> getTable() {
        return tableMap;
    }

    public int getAcceptState() {
        return acceptState;
    }

    public void setAcceptState(int acceptState) {
        this.acceptState = acceptState;
    }

    public void addTransition(int stateIndex, AbstractSymbol abstractSymbol, int nextStateIndex) {
        Transition transition;
        if (abstractSymbol.getType() == AbstractSymbol.NON_TERMINAL_SYMBOL) {
            transition = new Transition(Transition.GOTO, nextStateIndex);
        } else {
            transition = new Transition(Transition.SHIFT, nextStateIndex);
        }
        if (!tableMap.containsKey(stateIndex)) {
            tableMap.put(stateIndex, new HashMap<>());
        }
        tableMap.get(stateIndex).put(abstractSymbol, transition);
    }

    public void addTransition(int stateIndex, AbstractSymbol abstractSymbol, Production production) {
        Transition transition = new Transition(production);
        if (!tableMap.containsKey(stateIndex)) {
            tableMap.put(stateIndex, new HashMap<>());
        }
        tableMap.get(stateIndex).put(abstractSymbol, transition);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<AbstractSymbol> abstractSymbolSet = new HashSet<>();
        for (int i : tableMap.keySet()) {
            abstractSymbolSet.addAll(tableMap.get(i).keySet());
        }
        List<Integer> sortedStateIndices = new ArrayList<>(tableMap.keySet());
        Collections.sort(sortedStateIndices);
        List<AbstractSymbol> abstractSymbolList = new ArrayList<>(abstractSymbolSet);
        List<AbstractSymbol> abstractTerminalSymbols = new ArrayList<>(grammar.getSymbolPool().getTerminalSymbols());
        List<AbstractSymbol> abstractNonTerminalSymbols = new ArrayList<>(grammar.getSymbolPool().getNonTerminalSymbols());
        List<AbstractSymbol> listForOrder = new ArrayList<>();
        listForOrder.addAll(abstractTerminalSymbols);
        listForOrder.addAll(abstractNonTerminalSymbols);
        abstractSymbolList.sort(Comparator.comparingInt(listForOrder::indexOf));
        for (AbstractSymbol abstractSymbol : abstractSymbolList) {
            stringBuilder.append("\t");
            stringBuilder.append(abstractSymbol.getName());
        }
        stringBuilder.append("\n");
        for (int i : sortedStateIndices) {
            stringBuilder.append(i);
            for (AbstractSymbol abstractSymbol : abstractSymbolList) {
                stringBuilder.append("\t");
                if (tableMap.get(i).containsKey(abstractSymbol)) {
                    try {
                        if (acceptState == i && abstractSymbol.equals(grammar.getSymbolPool().getTerminalSymbol("$"))) {
                            stringBuilder.append("acc");
                        } else {
                            stringBuilder.append(tableMap.get(i).get(abstractSymbol));
                        }
                    } catch (AnalysisException e) {
                        e.printStackTrace();
                    }
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}

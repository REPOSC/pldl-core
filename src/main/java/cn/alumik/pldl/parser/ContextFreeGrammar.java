package cn.alumik.pldl.parser;

import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.symbol.AbstractNonTerminalSymbol;
import cn.alumik.pldl.symbol.AbstractSymbol;
import cn.alumik.pldl.symbol.AbstractTerminalSymbol;
import cn.alumik.pldl.symbol.SymbolPool;
import cn.alumik.pldl.util.yaml.ConfigLoader;

import java.util.*;

public class ContextFreeGrammar {

    private static final String START_SYMBOL = "_S";

    private AbstractNonTerminalSymbol startSymbol;

    private SymbolPool symbolPool;

    private List<Production> productions;

    private ParseTable parseTable;

    public ContextFreeGrammar(ConfigLoader configLoader) throws AnalysisException {
        symbolPool = new SymbolPool(
                configLoader.getTerminalSymbols(),
                configLoader.getNonTerminalSymbols(),
                START_SYMBOL);
        initProductions(configLoader.getProductions(), configLoader.getStartSymbol());
    }

    public ContextFreeGrammar(
            Set<String> terminalSymbols,
            Set<String> nonTerminalSymbols,
            String startSymbol) throws AnalysisException {
        symbolPool = new SymbolPool(terminalSymbols, nonTerminalSymbols, START_SYMBOL);
        this.startSymbol = symbolPool.getNonTerminalSymbol(startSymbol);
    }

    public AbstractNonTerminalSymbol getStartSymbol() {
        return startSymbol;
    }

    public SymbolPool getSymbolPool() {
        return symbolPool;
    }

    List<Production> getProductions() {
        return productions;
    }

    public void setProductions(List<? extends Production> productions) {
        this.productions = new ArrayList<>(productions);
    }

    public ParseTable getParseTable() {
        return parseTable;
    }

    private void initProductions(List<String> prodStrList, String startSymbol) throws AnalysisException {
        productions = new ArrayList<>();
        for (String prodStr : prodStrList) {
            Production production = Production.fromString(prodStr, this);
            productions.add(production);
        }
        augmentGrammar(startSymbol);
    }

    private void augmentGrammar(String startSymbol) throws AnalysisException {
        if (!symbolPool.getNonTerminalSymbolNames().contains(startSymbol)) {
            throw new AnalysisException("解析失败，开始符号不是非终结符", null);
        }
        AbstractNonTerminalSymbol oldStartSymbol = symbolPool.getNonTerminalSymbol(startSymbol);
        AbstractNonTerminalSymbol newStartSymbol = new AbstractNonTerminalSymbol(START_SYMBOL);
        symbolPool.addNonTerminalSymbol(newStartSymbol);
        List<AbstractSymbol> to = new ArrayList<>();
        to.add(oldStartSymbol);
        Production production = new Production(newStartSymbol, to);
        productions.add(0, production);
        this.startSymbol = newStartSymbol;
    }

    private void initSymbolProductions() {
        for (Production production : productions) {
            AbstractNonTerminalSymbol from = (AbstractNonTerminalSymbol) production.from();
            from.getProductions().add(production);
        }
    }

    private void initSymbolNullable() throws AnalysisException {
        Set<AbstractSymbol> tmpNullableSymbols = new HashSet<>();
        Set<AbstractSymbol> nullableSymbols = new HashSet<>();
        tmpNullableSymbols.add(symbolPool.getTerminalSymbol("null"));

        Map<AbstractSymbol, Set<Production>> symbolProductions = new HashMap<>();
        for (Production production : productions) {
            AbstractSymbol from = production.from();
            if (!symbolProductions.containsKey(from)) {
                symbolProductions.put(from, new HashSet<>());
            }
            symbolProductions.get(from).add(new Production(production));
        }

        while (!tmpNullableSymbols.isEmpty()) {
            Set<AbstractSymbol> nextTmpNullableSymbols = new HashSet<>();
            for (AbstractSymbol from : symbolProductions.keySet()) {
                for (Production production : symbolProductions.get(from)) {
                    List<AbstractSymbol> to = production.to();
                    for (AbstractSymbol abstractSymbol : tmpNullableSymbols) {
                        to.remove(abstractSymbol);
                    }
                    if (to.size() <= 0) {
                        nextTmpNullableSymbols.add(from);
                        break;
                    }
                }
            }
            symbolProductions.keySet().removeAll(nextTmpNullableSymbols);
            nullableSymbols.addAll(nextTmpNullableSymbols);
            tmpNullableSymbols = nextTmpNullableSymbols;
        }
        for (AbstractSymbol abstractSymbol : nullableSymbols) {
            ((AbstractNonTerminalSymbol) abstractSymbol).setNullable(true);
        }
    }

    private void initSymbolFirstSet() throws AnalysisException {
        initSymbolNullable();
        Map<AbstractNonTerminalSymbol, Set<AbstractNonTerminalSymbol>> connections = new HashMap<>();
        Map<AbstractNonTerminalSymbol, Set<AbstractTerminalSymbol>> firstSets = new HashMap<>();
        Map<AbstractNonTerminalSymbol, Set<AbstractTerminalSymbol>> tmpFirstSets = new HashMap<>();
        AbstractTerminalSymbol nullSymbol = symbolPool.getTerminalSymbol("null");
        for (Production production : productions) {
            for (AbstractSymbol abstractSymbol : production.to()) {
                AbstractNonTerminalSymbol from = (AbstractNonTerminalSymbol) production.from();
                if (abstractSymbol.getType() == AbstractSymbol.NON_TERMINAL_SYMBOL) {
                    AbstractNonTerminalSymbol abstractNonTerminalSymbol = (AbstractNonTerminalSymbol) abstractSymbol;
                    if (!connections.containsKey(abstractNonTerminalSymbol)) {
                        connections.put(abstractNonTerminalSymbol, new HashSet<>());
                    }
                    connections.get(abstractNonTerminalSymbol).add(from);
                    if (!(abstractNonTerminalSymbol.isNullable())) {
                        break;
                    }
                } else if (!abstractSymbol.equals(nullSymbol)) {
                    AbstractTerminalSymbol abstractTerminalSymbol = (AbstractTerminalSymbol) abstractSymbol;
                    if (!tmpFirstSets.containsKey(from)) {
                        tmpFirstSets.put(from, new HashSet<>());
                    }
                    tmpFirstSets.get(from).add(abstractTerminalSymbol);
                    break;
                } else {
                    break;
                }
            }
        }
        while (!tmpFirstSets.isEmpty()) {
            Map<AbstractNonTerminalSymbol, Set<AbstractTerminalSymbol>> newTmpFirstSets = new HashMap<>();
            for (AbstractNonTerminalSymbol abstractNonTerminalSymbol : tmpFirstSets.keySet()) {
                if (!firstSets.containsKey(abstractNonTerminalSymbol)) {
                    firstSets.put(abstractNonTerminalSymbol, new HashSet<>());
                }
                firstSets.get(abstractNonTerminalSymbol).addAll(tmpFirstSets.get(abstractNonTerminalSymbol));
                if (connections.containsKey(abstractNonTerminalSymbol)) {
                    for (AbstractNonTerminalSymbol target : connections.get(abstractNonTerminalSymbol)) {
                        if (!newTmpFirstSets.containsKey(target)) {
                            newTmpFirstSets.put(target, new HashSet<>());
                        }
                        newTmpFirstSets.get(target).addAll(tmpFirstSets.get(abstractNonTerminalSymbol));
                    }
                }
            }
            tmpFirstSets.clear();
            for (AbstractNonTerminalSymbol abstractNonTerminalSymbol : newTmpFirstSets.keySet()) {
                if (firstSets.containsKey(abstractNonTerminalSymbol)) {
                    newTmpFirstSets.get(abstractNonTerminalSymbol).removeAll(firstSets.get(abstractNonTerminalSymbol));
                }
                if (newTmpFirstSets.get(abstractNonTerminalSymbol).size() > 0) {
                    tmpFirstSets.put(abstractNonTerminalSymbol, newTmpFirstSets.get(abstractNonTerminalSymbol));
                }
            }
        }
        for (AbstractNonTerminalSymbol abstractNonTerminalSymbol : symbolPool.getNonTerminalSymbols()) {
            if (!firstSets.containsKey(abstractNonTerminalSymbol)) {
                firstSets.put(abstractNonTerminalSymbol, new HashSet<>());
            }
            if (abstractNonTerminalSymbol.isNullable()) {
                firstSets.get(abstractNonTerminalSymbol).add(nullSymbol);
            }
            abstractNonTerminalSymbol.setFirstSet(firstSets.get(abstractNonTerminalSymbol));
        }
    }

    public void initParseTable() throws AnalysisException {
        initSymbolProductions();
        initSymbolFirstSet();
        List<ParseState> stateList = new ArrayList<>();
        Map<ParseState, Integer> stateMap = new HashMap<>();

        ParseState startState = new ParseState(this);
        for (Production production : productions) {
            if (production.from().equals(startSymbol)) {
                startState.addItem(new Item(production, symbolPool.getTerminalSymbol("$")));
            }
        }
        startState.makeClosure();
        stateList.add(startState);
        stateMap.put(startState, 0);

        ParseTable parseTable = new ParseTable(this);
        for (int i = 0; i < stateList.size(); i++) {
            ParseState currentState = stateList.get(i);
            Set<Item> items = currentState.getItems();
            Map<AbstractSymbol, Set<Item>> groupedItems = new HashMap<>();
            for (Item item : items) {
                if (item.isNotEnded()) {
                    if (!groupedItems.containsKey(item.getNextSymbol())) {
                        groupedItems.put(item.getNextSymbol(), new HashSet<>());
                    }
                    groupedItems.get(item.getNextSymbol()).add(item);
                } else {
                    if (!groupedItems.containsKey(symbolPool.getTerminalSymbol("null"))) {
                        groupedItems.put(symbolPool.getTerminalSymbol("null"), new HashSet<>());
                    }
                    groupedItems.get(symbolPool.getTerminalSymbol("null")).add(item);
                }
            }
            for (AbstractSymbol abstractSymbol : groupedItems.keySet()) {
                if (abstractSymbol.equals(symbolPool.getTerminalSymbol("null"))) {
                    for (Item item : groupedItems.get(abstractSymbol)) {
                        parseTable.addTransition(i, item.getLookAhead(), item.getProduction());
                        if (item.getLookAhead().equals(symbolPool.getTerminalSymbol("$"))
                                && item.getProduction().from().equals(startSymbol)) {
                            parseTable.setAcceptState(i);
                        }
                    }
                } else {
                    ParseState parseState = new ParseState(this);
                    for (Item item : groupedItems.get(abstractSymbol)) {
                        parseState.addItem(item.getNextItem());
                    }
                    parseState.makeClosure();
                    if (!stateMap.containsKey(parseState)) {
                        stateMap.put(parseState, stateList.size());
                        stateList.add(parseState);
                    }
                    parseTable.addTransition(i, abstractSymbol, stateMap.get(parseState));
                }
            }
        }
        this.parseTable = parseTable;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("上下文无关文法包含以下产生式:");
        for (Production production : productions) {
            stringBuilder.append("\n");
            stringBuilder.append(production);
        }
        return stringBuilder.toString();
    }
}

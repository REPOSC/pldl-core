package cn.alumik.pldl.symbol;

import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.util.spring.EnvUtil;

import java.util.*;

public class SymbolPool {

    private Map<String, AbstractTerminalSymbol> terminalSymbols;

    private Map<String, AbstractNonTerminalSymbol> nonTerminalSymbols;

    public SymbolPool(
            Set<String> terminalSymbols,
            Set<String> nonTerminalSymbols, String startSymbol) throws AnalysisException {
        Map<String, String> reservedNames = Map.of(
                "null", "空串",
                "$", "产生式结束",
                startSymbol, "增广后的开始符号");
        for (String name : reservedNames.keySet()) {
            if (terminalSymbols.contains(name) || nonTerminalSymbols.contains(name)) {
                throw new AnalysisException(
                        String.format(EnvUtil.getProperty("exception.lexer.INVALID_NAME"), name, reservedNames.get(name)),
                        null);
            }
        }
        initTerminalSymbols(terminalSymbols);
        initNonTerminalSymbols(nonTerminalSymbols);
    }

    private void initTerminalSymbols(Set<String> terminalSymbols) {
        this.terminalSymbols = new HashMap<>();
        for (String name : terminalSymbols) {
            this.terminalSymbols.put(name, new AbstractTerminalSymbol(name));
        }
        this.terminalSymbols.put("null", AbstractTerminalSymbol.Null());
        this.terminalSymbols.put("$", AbstractTerminalSymbol.End());
    }

    private void initNonTerminalSymbols(Set<String> nonTerminalSymbols) {
        this.nonTerminalSymbols = new HashMap<>();
        for (String name : nonTerminalSymbols) {
            this.nonTerminalSymbols.put(name, new AbstractNonTerminalSymbol(name));
        }
    }

    public Set<AbstractTerminalSymbol> getTerminalSymbols() {
        return new HashSet<>(terminalSymbols.values());
    }

    public AbstractTerminalSymbol getTerminalSymbol(String name) throws AnalysisException {
        if (terminalSymbols.containsKey(name)) {
            return terminalSymbols.get(name);
        }
        throw new AnalysisException(
                String.format(EnvUtil.getProperty("exception.lexer.TERMINAL_SYMBOL_NOT_EXIST"), name), null);
    }

    public Set<String> getNonTerminalSymbolNames() {
        return nonTerminalSymbols.keySet();
    }

    public Set<AbstractNonTerminalSymbol> getNonTerminalSymbols() {
        return new HashSet<>(nonTerminalSymbols.values());
    }

    public AbstractNonTerminalSymbol getNonTerminalSymbol(String name) throws AnalysisException {
        if (nonTerminalSymbols.containsKey(name)) {
            return nonTerminalSymbols.get(name);
        }
        throw new AnalysisException(
                String.format(EnvUtil.getProperty("exception.lexer.NON_TERMINAL_SYMBOL_NOT_EXIST"), name), null);
    }

    public void addNonTerminalSymbol(AbstractNonTerminalSymbol abstractNonTerminalSymbol) {
        if (!nonTerminalSymbols.containsKey(abstractNonTerminalSymbol.getName())) {
            nonTerminalSymbols.put(abstractNonTerminalSymbol.getName(), abstractNonTerminalSymbol);
        }
    }

    public AbstractSymbol getSymbol(String name) throws AnalysisException {
        if (terminalSymbols.containsKey(name)) {
            return terminalSymbols.get(name);
        } else if (nonTerminalSymbols.containsKey(name)) {
            return nonTerminalSymbols.get(name);
        }
        throw new AnalysisException(
                String.format(EnvUtil.getProperty("exception.lexer.SYMBOL_NOT_EXIST"), name), null);
    }
}

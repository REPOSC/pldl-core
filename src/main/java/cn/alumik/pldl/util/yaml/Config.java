package cn.alumik.pldl.util.yaml;

import java.util.*;

public class Config {

    private Set<String> nonTerminalSymbols;

    private Map<String, String> terminalSymbols;

    private Set<String> ignoredSymbols;

    private String startSymbol;

    private List<String> productions;

    Set<String> getNonTerminalSymbols() {
        if (nonTerminalSymbols != null) {
            return nonTerminalSymbols;
        }
        return new LinkedHashSet<>();
    }

    public void setNonTerminalSymbols(Set<String> nonTerminalSymbols) {
        this.nonTerminalSymbols = nonTerminalSymbols;
    }

    public Map<String, String> getTerminalSymbols() {
        if (terminalSymbols != null) {
            return terminalSymbols;
        }
        return new LinkedHashMap<>();
    }

    public void setTerminalSymbols(Map<String, String> terminalSymbols) {
        this.terminalSymbols = terminalSymbols;
    }

    Set<String> getIgnoredSymbols() {
        if (ignoredSymbols != null) {
            return ignoredSymbols;
        }
        return new LinkedHashSet<>();
    }

    public void setIgnoredSymbols(Set<String> ignoredSymbols) {
        this.ignoredSymbols = ignoredSymbols;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(String startSymbol) {
        this.startSymbol = startSymbol;
    }

    List<String> getProductions() {
        if (productions != null) {
            return productions;
        }
        return new ArrayList<>();
    }

    public void setProductions(List<String> productions) {
        this.productions = productions;
    }
}

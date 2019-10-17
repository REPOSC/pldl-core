package cn.alumik.pldl.parser;

import cn.alumik.pldl.symbol.AbstractNonTerminalSymbol;
import cn.alumik.pldl.symbol.AbstractSymbol;
import cn.alumik.pldl.exception.AnalysisException;

import java.util.ArrayList;
import java.util.List;

public class Production {

    private AbstractSymbol from;

    private List<AbstractSymbol> to;

    protected Production() {
    }

    public Production(AbstractSymbol from, List<AbstractSymbol> to) {
        this.from = from;
        this.to = to;
    }

    public Production(Production production) {
        from = production.from;
        to = new ArrayList<>();
        to.addAll(production.to);
    }

    public static Production fromString(String input, ContextFreeGrammar grammar) throws AnalysisException {
        Production production = new Production();
        if (!input.contains("->")) {
            throw new AnalysisException("产生式没有使用箭头\"->\"分割为两部分，请将产生式写成\"A -> c B d\"这样的格式", null);
        }
        String[] parts = input.split("->");
        if (parts.length != 2) {
            throw new AnalysisException("产生式没有使用箭头\"->\"分割为两部分，请将产生式写成\"A -> c B d\"这样的格式", null);
        }
        String[] fromStr = parts[0].trim().split(" +");
        String[] toStr = parts[1].trim().split(" +");
        if (fromStr.length != 1) {
            throw new AnalysisException("产生式左部不是 1 个符号，上下文无关文法的产生式左部必须有且只有一个非终结符", null);
        }
        try {
            production.from = grammar.getSymbolPool().getNonTerminalSymbol(fromStr[0]);
        } catch (AnalysisException e) {
            throw new AnalysisException("产生式左部不是非终结符，因此这不是一个合法的产生式", e);
        }
        production.to = new ArrayList<>();
        if (toStr.length == 1 && toStr[0].equals("null")) {
            production.to.add(grammar.getSymbolPool().getTerminalSymbol("null"));
            return production;
        } else if (toStr.length > 0 && toStr[0].length() > 0) {
            for (String string : toStr) {
                try {
                    production.to.add(grammar.getSymbolPool().getSymbol(string));
                } catch (AnalysisException e) {
                    throw new AnalysisException(string + " 既不能被识别为终结符，也不能被识别为非终结符，是否忘记使用空格隔开？", e);
                }
            }
            return production;
        } else {
            throw new AnalysisException("产生式右部没有任何字符，如果你需要表示空产生式，请将产生式右部设置为 null", null);
        }
    }

    public AbstractSymbol from() {
        return from;
    }

    public List<AbstractSymbol> to() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Production) {
            Production production = (Production) obj;
            if (!from.equals(production.from) || to.size() != production.to.size()) {
                return false;
            }
            for (int i = 0; i < to.size(); i++) {
                if (!to.get(i).equals(production.to.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("产生式: ");
        result.append(from.toString());
        result.append(" ->");
        for (AbstractSymbol abstractSymbol : to) {
            result.append(" ");
            result.append(abstractSymbol.toString());
        }
        return result.toString();
    }

    @Override
    public int hashCode() {
        int hash = from.hashCode();
        for (AbstractSymbol abstractSymbol : to) {
            hash ^= abstractSymbol.hashCode();
        }
        return hash;
    }
}

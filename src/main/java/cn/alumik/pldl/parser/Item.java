package cn.alumik.pldl.parser;

import cn.alumik.pldl.symbol.AbstractSymbol;
import cn.alumik.pldl.symbol.AbstractTerminalSymbol;

class Item {

    private int dot;

    private final Production production;

    private final AbstractTerminalSymbol lookAhead;

    Item(Production production, AbstractTerminalSymbol lookAhead) {
        this.production = production;
        this.lookAhead = lookAhead;
        this.dot = 0;
    }

    AbstractSymbol getNextSymbol() {
        return production.to().get(dot);
    }

    int getDot() {
        return dot;
    }

    public Production getProduction() {
        return production;
    }

    AbstractTerminalSymbol getLookAhead() {
        return lookAhead;
    }

    boolean isNotEnded() {
        return dot < production.to().size() && !production.to().get(0).getName().equals("null");
    }

    Item getNextItem() {
        Item item = new Item(production, lookAhead);
        item.dot = dot + 1;
        return item;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item item = (Item) obj;
            return item.dot == dot
                    && item.production.equals(production)
                    && item.lookAhead.equals(lookAhead);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return production.hashCode() ^ dot;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(production.from());
        stringBuilder.append(" ->");
        for (int i = 0; i < dot; ++i) {
            stringBuilder.append(" ");
            stringBuilder.append(production.to().get(i));
        }
        stringBuilder.append(" ·");
        for (int i = dot; i < production.to().size(); i++) {
            stringBuilder.append(" ");
            stringBuilder.append(production.to().get(i));
        }
        stringBuilder.append("（展望符: ");
        stringBuilder.append(lookAhead);
        stringBuilder.append("）");
        return stringBuilder.toString();
    }
}

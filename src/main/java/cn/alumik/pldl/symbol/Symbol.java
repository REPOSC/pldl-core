package cn.alumik.pldl.symbol;

import java.util.HashMap;
import java.util.Map;

public abstract class Symbol {

    private AbstractSymbol abstractSymbol;

    private final Map<String, Object> properties = new HashMap<>();

    public AbstractSymbol getAbstractSymbol() {
        return abstractSymbol;
    }

    void setAbstractSymbol(AbstractSymbol abstractSymbol) {
        this.abstractSymbol = abstractSymbol;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol) {
            Symbol symbol = (Symbol) obj;
            return abstractSymbol.equals(symbol.getAbstractSymbol()) && properties.equals(symbol.getProperties());
        }
        return false;
    }

    @Override
    public String toString() {
        return abstractSymbol.toString() + ": " + properties.toString();
    }

    @Override
    public int hashCode() {
        return abstractSymbol.hashCode() ^ properties.hashCode();
    }
}

package cn.alumik.pldl.symbol;

public abstract class AbstractSymbol {

    static final int TERMINAL_SYMBOL = 0x01;

    public static final int NON_TERMINAL_SYMBOL = 0xff;

    private String name;

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public abstract int getType();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractSymbol) {
            AbstractSymbol abstractSymbol = (AbstractSymbol) obj;
            return getType() == abstractSymbol.getType()
                    && name.equals(abstractSymbol.name);
        }
        return false;
    }

    @Override
    public String toString() {
        return (getType() == TERMINAL_SYMBOL ? "终结符: " : "非终结符: ") + name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

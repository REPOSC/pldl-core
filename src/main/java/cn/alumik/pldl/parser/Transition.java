package cn.alumik.pldl.parser;

import cn.alumik.pldl.util.spring.AppContextUtil;

public class Transition {

    public static final int SHIFT = 0x01;

    public static final int GOTO = 0x02;

    public static final int REDUCE = 0x03;

    private final int operation;

    private int nextState;

    private Production reduceProduction;

    Transition(int operation, int nextState) {
        this.operation = operation;
        this.nextState = nextState;
    }

    Transition(Production reduceProduction) {
        this.operation = REDUCE;
        this.reduceProduction = reduceProduction;
    }

    public int getOperation() {
        return operation;
    }

    public Integer getNextState() {
        if (operation == SHIFT || operation == GOTO) {
            return nextState;
        }
        return null;
    }

    public Production getReduceProduction() {
        return reduceProduction;
    }

    @Override
    public String toString() {
        Parser parser = AppContextUtil.getBean(Parser.class);
        if (operation == REDUCE) {
            return "r" + (parser.getGrammar().getProductions().indexOf(reduceProduction) + 1);
        } else {
            return "s" + nextState;
        }
    }
}

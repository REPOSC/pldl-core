package cn.alumik.pldl.lexer;

import cn.alumik.pldl.lexer.statemachine.FSMState;
import cn.alumik.pldl.lexer.statemachine.NFA;
import cn.alumik.pldl.symbol.*;
import cn.alumik.pldl.exception.ParsingException;
import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.parser.ContextFreeGrammar;
import cn.alumik.pldl.parser.Transition;
import cn.alumik.pldl.util.spring.EnvUtil;

import java.util.*;

abstract class AbstractRegexEngine {

    private String acceptingRule;

    private String regex;

    private NFA nfa;

    private ContextFreeGrammar grammar;

    AbstractRegexEngine(String acceptingRule, String regex) throws ParsingException, AnalysisException {
        initGrammar();
        this.acceptingRule = acceptingRule;
        this.regex = regex;
        initNFA();
    }

    String getRegex() {
        return regex;
    }

    NFA getNFA() {
        return nfa;
    }

    ContextFreeGrammar getGrammar() {
        return grammar;
    }

    void setGrammar(ContextFreeGrammar grammar) {
        this.grammar = grammar;
    }

    private void initNFA() throws ParsingException, AnalysisException {
        Map<Integer, Map<AbstractSymbol, Transition>> parseTable = grammar.getParseTable().getTable();
        List<Symbol> symbols = getSymbols();
        AbstractSymbol startSymbol = grammar.getStartSymbol();

        Stack<Integer> stateStack = new Stack<>();
        Stack<Symbol> symbolStack = new Stack<>();
        Stack<NFA> nfaStack = new Stack<>();

        stateStack.push(0);
        int index = 0;

        while (index != symbols.size() - 1 || !symbols.get(index).getAbstractSymbol().equals(startSymbol)) {
            int currentState = stateStack.peek();
            Symbol currentSymbol = index < symbols.size() ?
                    symbols.get(index) : new TerminalSymbol(grammar.getSymbolPool().getTerminalSymbol("$"));
            Transition transition = parseTable.get(currentState).get(currentSymbol.getAbstractSymbol());

            if (transition == null) {
                throw new ParsingException(
                        String.format(EnvUtil.getProperty("exception.parser.CANNOT_CONTINUE"), index + 1, currentSymbol),
                        null);
            } else {
                switch (transition.getOperation()) {
                    case Transition.SHIFT:
                        nfaStack.push(new NFA());
                        symbolStack.push(currentSymbol);
                    case Transition.GOTO:
                        stateStack.push(transition.getNextState());
                        index++;
                        break;
                    case Transition.REDUCE:
                        RegexProduction regexProduction = (RegexProduction) transition.getReduceProduction();
                        List<NFA> nodes = new ArrayList<>();
                        List<Symbol> children = new ArrayList<>();
                        for (AbstractSymbol ignored : regexProduction.to()) {
                            stateStack.pop();
                            nodes.add(nfaStack.pop());
                            children.add(symbolStack.pop());
                        }
                        Collections.reverse(nodes);
                        Collections.reverse(children);
                        nfaStack.push(regexProduction.getNFA(nodes, children));
                        Symbol newSymbol = new NonTerminalSymbol((AbstractNonTerminalSymbol) regexProduction.from());
                        symbolStack.push(newSymbol);
                        index--;
                        symbols.set(index, newSymbol);
                }
            }
        }
        if (nfaStack.size() != 1) {
            throw new ParsingException(
                    String.format(EnvUtil.getProperty("exception.parser.CANNOT_FINISH"), nfaStack), null);
        } else {
            nfa = nfaStack.pop();
            for (FSMState state : nfa.getFinalStates()) {
                state.addAcceptingRule(acceptingRule);
            }
        }
    }

    protected abstract void initGrammar() throws AnalysisException;

    protected abstract List<Symbol> getSymbols() throws ParsingException, AnalysisException;
}

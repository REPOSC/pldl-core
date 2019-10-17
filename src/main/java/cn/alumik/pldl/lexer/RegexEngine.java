package cn.alumik.pldl.lexer;

import cn.alumik.pldl.lexer.statemachine.FSMState;
import cn.alumik.pldl.lexer.statemachine.NFA;
import cn.alumik.pldl.parser.ContextFreeGrammar;
import cn.alumik.pldl.parser.Production;
import cn.alumik.pldl.symbol.AbstractTerminalSymbol;
import cn.alumik.pldl.symbol.Symbol;
import cn.alumik.pldl.exception.ParsingException;
import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.symbol.TerminalSymbol;
import cn.alumik.pldl.util.spring.EnvUtil;

import java.util.*;

public class RegexEngine extends AbstractRegexEngine {

    private static final Set<String> TERMINAL_SYMBOLS = new HashSet<>(
            Arrays.asList("|", "(", ")", "*", "+", "[", "]", "-", "char", "^", "."));

    private static final Set<String> NON_TERMINAL_SYMBOLS = new HashSet<>(
            Arrays.asList("E", "T", "F", "Fx", "Fxs"));

    private static final String START_SYMBOL = "E";

    RegexEngine(String acceptingRule, String regex) throws ParsingException, AnalysisException {
        super(acceptingRule, regex);
    }

    @Override
    protected void initGrammar() throws AnalysisException {
        List<RegexProduction> regexRules = new ArrayList<>();
        ContextFreeGrammar grammar = new ContextFreeGrammar(TERMINAL_SYMBOLS, NON_TERMINAL_SYMBOLS, START_SYMBOL);

        regexRules.add(new RegexProduction(Production.fromString("E -> E | T", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                FSMState startState = FSMState.makeState();
                FSMState finalState = FSMState.makeState();
                finalState.setFinal(true);
                startState.addTransition('\0', nodes.get(0).getStartState());
                startState.addTransition('\0', nodes.get(2).getStartState());
                for (FSMState state : nodes.get(0).getFinalStates()) {
                    state.setFinal(false);
                    state.addTransition('\0', finalState);
                }
                for (FSMState state : nodes.get(2).getFinalStates()) {
                    state.setFinal(false);
                    state.addTransition('\0', finalState);
                }
                NFA nfa = new NFA(startState);
                nfa.addFinalState(finalState);
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("E -> T", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(0);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("T -> T F", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                FSMState startState = FSMState.makeState();
                FSMState finalState = FSMState.makeState();
                finalState.setFinal(true);
                startState.addTransition('\0', nodes.get(0).getStartState());
                for (FSMState state : nodes.get(0).getFinalStates()) {
                    state.setFinal(false);
                    state.addTransition('\0', nodes.get(1).getStartState());
                }
                for (FSMState state : nodes.get(1).getFinalStates()) {
                    state.setFinal(false);
                    state.addTransition('\0', finalState);
                }
                NFA nfa = new NFA(startState);
                nfa.addFinalState(finalState);
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("T -> F", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(0);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> ( E )", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(1);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> F *", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                FSMState startState = FSMState.makeState();
                FSMState finalState = FSMState.makeState();
                finalState.setFinal(true);
                startState.addTransition('\0', nodes.get(0).getStartState());
                for (FSMState state : nodes.get(0).getFinalStates()) {
                    state.addTransition('\0', nodes.get(0).getStartState());
                    state.addTransition('\0', finalState);
                    state.setFinal(false);
                }
                startState.addTransition('\0', finalState);
                NFA nfa = new NFA(startState);
                nfa.addFinalState(finalState);
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> F +", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                FSMState startState = FSMState.makeState();
                FSMState finalState = FSMState.makeState();
                finalState.setFinal(true);
                startState.addTransition('\0', nodes.get(0).getStartState());
                for (FSMState state : nodes.get(0).getFinalStates()) {
                    state.addTransition('\0', nodes.get(0).getStartState());
                    state.addTransition('\0', finalState);
                    state.setFinal(false);
                }
                NFA nfa = new NFA(startState);
                nfa.addFinalState(finalState);
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> Fx", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(0);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fx -> .", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                FSMState startState = FSMState.makeState();
                for (char c = 1; c < 127; ++c) {
                    startState.addTransition(c, nodes.get(0).getStartState());
                }
                nodes.get(0).getStartState().setFinal(true);
                NFA nfa = new NFA(startState);
                nfa.addFinalState(nodes.get(0).getStartState());
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fx -> char", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                FSMState startState = FSMState.makeState();
                startState.addTransition(
                        ((String) children.get(0).getProperties().get("value")).charAt(0),
                        nodes.get(0).getStartState());
                nodes.get(0).getStartState().setFinal(true);
                NFA nfa = new NFA(startState);
                nfa.addFinalState(nodes.get(0).getStartState());
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fx -> char - char", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                char tmpStartChar = ((String) children.get(0).getProperties().get("value")).charAt(0);
                char tmpEndChar = ((String) children.get(2).getProperties().get("value")).charAt(0);
                char startChar = (char) Math.min(tmpStartChar, tmpEndChar);
                char endChar = (char) Math.max(tmpStartChar, tmpEndChar);

                FSMState startState = FSMState.makeState();
                for (char c = startChar; c <= endChar; c++) {
                    startState.addTransition(c, nodes.get(0).getStartState());
                }
                nodes.get(0).getStartState().setFinal(true);
                NFA nfa = new NFA(startState);
                nfa.addFinalState(nodes.get(0).getStartState());
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fxs -> Fxs Fx", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                NFA nfa = nodes.get(0);
                FSMState startState = nfa.getStartState();
                startState.getTransitions().putAll(nodes.get(1).getStartState().getTransitions());
                for (FSMState state : nodes.get(1).getFinalStates()) {
                    for (FSMState state1 : nodes.get(0).getFinalStates()) {
                        state.addTransition('\0', state1);
                        state.setFinal(false);
                    }
                }
                return nfa;
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("Fxs -> Fx", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(0);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> [ Fxs ]", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                return nodes.get(1);
            }
        });

        regexRules.add(new RegexProduction(Production.fromString("F -> [ ^ Fxs ]", grammar)) {
            @Override
            public NFA getNFA(List<NFA> nodes, List<Symbol> children) {
                FSMState startState = FSMState.makeState();
                Set<Character> chars = new HashSet<>();
                for (char c = 1; c < 127; ++c) {
                    chars.add(c);
                }
                chars.removeAll(nodes.get(2).getStartState().getTransitions().keySet());
                FSMState nextState = nodes.get(2).getStartState().getTransitions().values()
                        .iterator().next().iterator().next();
                for (char c : chars) {
                    startState.addTransition(c, nextState);
                }
                NFA nfa = new NFA(startState);
                nfa.setFinalStates(nodes.get(2).getFinalStates());
                return nfa;
            }
        });

        grammar.setProductions(regexRules);
        grammar.initParseTable();
        setGrammar(grammar);
    }

    @Override
    protected List<Symbol> getSymbols() throws ParsingException, AnalysisException {
        List<Symbol> symbols = new ArrayList<>();
        String regex = getRegex();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            switch (c) {
                case '\\':
                    i++;
                    if (i >= regex.length()) {
                        throw new ParsingException(EnvUtil.getProperty("exception.lexer.INVALID_REGEX"), null);
                    }
                    c = regex.charAt(i);
                    switch (c) {
                        case '-':
                        case '+':
                        case '*':
                        case '|':
                        case '[':
                        case ']':
                        case '(':
                        case '^':
                        case '.':
                        case ')':
                            symbols.add(makeSymbol(c));
                            break;
                        case 'r':
                            symbols.add(makeSymbol('\r'));
                            break;
                        case 'n':
                            symbols.add(makeSymbol('\n'));
                            break;
                        case 't':
                            symbols.add(makeSymbol('\t'));
                            break;
                        case 'f':
                            symbols.add(makeSymbol('\f'));
                            break;
                        case '\\':
                            symbols.add(makeSymbol('\\'));
                            break;
                    }
                    break;
                case '-':
                case '+':
                case '*':
                case '|':
                case '[':
                case ']':
                case '(':
                case '^':
                case '.':
                case ')':
                    AbstractTerminalSymbol abstractTerminalSymbol = getGrammar()
                            .getSymbolPool()
                            .getTerminalSymbol(String.valueOf(c));
                    TerminalSymbol terminalSymbol = new TerminalSymbol(abstractTerminalSymbol);
                    symbols.add(terminalSymbol);
                    break;
                case '\n':
                case '\r':
                    break;
                default:
                    symbols.add(makeSymbol(c));
            }
        }
        return symbols;
    }

    private TerminalSymbol makeSymbol(char c) throws AnalysisException {
        AbstractTerminalSymbol abstractTerminalSymbol = getGrammar().getSymbolPool().getTerminalSymbol("char");
        TerminalSymbol terminalSymbol = new TerminalSymbol(abstractTerminalSymbol);
        terminalSymbol.addProperty("value", String.valueOf(c));
        return terminalSymbol;
    }
}

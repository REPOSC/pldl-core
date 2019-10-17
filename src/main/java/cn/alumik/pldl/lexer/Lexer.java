package cn.alumik.pldl.lexer;

import cn.alumik.pldl.exception.ParsingException;
import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.lexer.statemachine.DFA;
import cn.alumik.pldl.lexer.statemachine.FSMState;
import cn.alumik.pldl.lexer.statemachine.NFA;
import cn.alumik.pldl.parser.Parser;
import cn.alumik.pldl.symbol.TerminalSymbol;
import cn.alumik.pldl.util.spring.EnvUtil;
import cn.alumik.pldl.util.yaml.ConfigLoader;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Lexer {

    private final ConfigLoader configLoader;

    private final Parser parser;

    private DFA dfa;

    private Map<String, String> acceptingRules = new LinkedHashMap<>();

    private Set<String> ignoredSymbols = new HashSet<>();

    private List<TerminalSymbol> result;

    public Lexer(ConfigLoader configLoader, Parser parser) {
        this.configLoader = configLoader;
        this.parser = parser;
    }

    void setDfa(DFA dfa) {
        this.dfa = dfa;
    }

    public Map<String, String> getAcceptingRules() {
        return acceptingRules;
    }

    public void setAcceptingRule(Map<String, String> acceptingRules) {
        this.acceptingRules = acceptingRules;
    }

    public Set<String> getIgnoredSymbols() {
        return ignoredSymbols;
    }

    public List<TerminalSymbol> getResult() {
        return result;
    }

    public void init() throws AnalysisException, ParsingException {
        initAcceptingRules();
        initDFA();
    }

    private void initAcceptingRules() {
        acceptingRules = configLoader.getAcceptingRules();
        ignoredSymbols = configLoader.getIgnoredSymbols();
    }

    private void initDFA() throws ParsingException, AnalysisException {
        List<NFA> nfas = new ArrayList<>();
        for (Map.Entry<String, String> rule : acceptingRules.entrySet()) {
            RegexEngine regexEngine = new RegexEngine(rule.getKey(), rule.getValue());
            nfas.add(regexEngine.getNFA());
        }
        FSMState startState = FSMState.makeState();
        for (NFA nfa : nfas) {
            startState.addTransition('\0', nfa.getStartState());
        }
        dfa = new DFA(new NFA(startState));
    }

    public void lex(String input) throws ParsingException, AnalysisException {
        result = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder(input);
        while (stringBuilder.length() != 0) {
            Map.Entry<String, Integer> result = dfa.match(stringBuilder.toString());
            if (result.getKey().equals("")) {
                throw new ParsingException(EnvUtil.getProperty("exception.lexer.CANNOT_CONTINUE"), null);
            }
            if (!ignoredSymbols.contains(result.getKey())) {
                String value = stringBuilder.substring(0, result.getValue());
                System.out.println(result.getKey() + ": " + value);
                TerminalSymbol terminalSymbol = new TerminalSymbol(
                        parser.getGrammar().getSymbolPool().getTerminalSymbol(result.getKey()));
                terminalSymbol.addProperty("value", value);
                this.result.add(terminalSymbol);
            }
            stringBuilder.delete(0, result.getValue());
        }
    }
}

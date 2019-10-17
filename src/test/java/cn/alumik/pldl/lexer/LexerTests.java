package cn.alumik.pldl.lexer;

import cn.alumik.pldl.exception.ParsingException;
import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.lexer.statemachine.DFA;
import cn.alumik.pldl.lexer.statemachine.FSMState;
import cn.alumik.pldl.lexer.statemachine.NFA;
import cn.alumik.pldl.parser.Parser;
import cn.alumik.pldl.symbol.AbstractTerminalSymbol;
import cn.alumik.pldl.symbol.TerminalSymbol;
import cn.alumik.pldl.util.yaml.ConfigLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LexerTests {

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private Lexer lexer;

    @Autowired
    private Parser parser;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private NFA makeNFA() {
        FSMState s0 = FSMState.makeState();
        FSMState s1 = FSMState.makeState();
        FSMState s2 = FSMState.makeState();
        FSMState s3 = FSMState.makeState();
        FSMState s4 = FSMState.makeState();
        FSMState s5 = FSMState.makeState();
        FSMState s6 = FSMState.makeState();
        FSMState s7 = FSMState.makeState();
        FSMState s8 = FSMState.makeState();
        FSMState s9 = FSMState.makeState();

        s0.addTransition('\0', s1);
        s1.addTransition('a', s2);
        s0.addTransition('\0', s3);
        s3.addTransition('a', s4);
        s4.addTransition('b', s5);
        s5.addTransition('c', s6);
        s0.addTransition('\0', s7);
        s7.addTransition('a', s8);
        s8.addTransition('b', s9);

        s2.setFinal(true);
        s5.setFinal(true);
        s6.setFinal(true);
        s9.setFinal(true);
        s2.addAcceptingRule("A");
        s5.addAcceptingRule("AB2");
        s6.addAcceptingRule("ABC");
        s9.addAcceptingRule("AB1");

        NFA nfa = new NFA(s0);
        nfa.addFinalState(s2);
        nfa.addFinalState(s5);
        nfa.addFinalState(s6);
        nfa.addFinalState(s9);
        return nfa;
    }

    private Lexer makeParserAndLexer() throws AnalysisException, ParsingException {
        configLoader.loadConfig("lexer-tests.yml");
        parser.init();
        lexer.init();
        lexer.setDfa(new DFA(makeNFA()));
        return lexer;
    }

    @Test(expected = ParsingException.class)
    public void testLexWithPredefinedDFA_ExceptionFront() throws ParsingException, AnalysisException {
        Lexer lexer = makeParserAndLexer();
        lexer.lex("babbebabc");
    }

    @Test(expected = ParsingException.class)
    public void testLexWithPredefinedDFA_ExceptionBack() throws ParsingException, AnalysisException {
        Lexer lexer = makeParserAndLexer();
        lexer.lex("aababcabefg");
    }

    @Test
    public void testLexWithIgnoredSymbolsAndPredefinedDFA() throws ParsingException, AnalysisException {
        Lexer lexer = makeParserAndLexer();
        lexer.lex("aabaabcabaab");

        assertEquals(
                "AB2: ab\nABC: abc\nAB2: ab\nAB2: ab\n",
                outContent.toString().replace("\r", ""));

        List<TerminalSymbol> result = new ArrayList<>();
        TerminalSymbol terminalSymbol1 = new TerminalSymbol(new AbstractTerminalSymbol("AB2"));
        terminalSymbol1.addProperty("value", "ab");
        result.add(terminalSymbol1);
        TerminalSymbol terminalSymbol2 = new TerminalSymbol(new AbstractTerminalSymbol("ABC"));
        terminalSymbol2.addProperty("value", "abc");
        result.add(terminalSymbol2);
        TerminalSymbol terminalSymbol3 = new TerminalSymbol(new AbstractTerminalSymbol("AB2"));
        terminalSymbol3.addProperty("value", "ab");
        result.add(terminalSymbol3);
        TerminalSymbol terminalSymbol4 = new TerminalSymbol(new AbstractTerminalSymbol("AB2"));
        terminalSymbol4.addProperty("value", "ab");
        result.add(terminalSymbol4);
        assertEquals(result, lexer.getResult());
    }
}

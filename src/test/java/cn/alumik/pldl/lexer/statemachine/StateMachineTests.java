package cn.alumik.pldl.lexer.statemachine;

import cn.alumik.pldl.lexer.Lexer;
import cn.alumik.pldl.lexer.statemachine.DFA;
import cn.alumik.pldl.lexer.statemachine.FSMState;
import cn.alumik.pldl.lexer.statemachine.NFA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StateMachineTests {

    @Autowired
    private Lexer lexer;

    private NFA makeNFA1() {
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
        FSMState s10 = FSMState.makeState();

        s0.addTransition('\0', s1);
        s1.addTransition('\0', s5);
        s1.addTransition('\0', s6);
        s5.addTransition('a', s2);
        s6.addTransition('b', s3);
        s2.addTransition('\0', s4);
        s3.addTransition('\0', s4);
        s4.addTransition('\0', s7);
        s7.addTransition('a', s8);
        s8.addTransition('b', s9);
        s9.addTransition('b', s10);
        s0.addTransition('\0', s7);
        s4.addTransition('\0', s1);

        s10.setFinal(true);
        s10.addAcceptingRule("(a|b)*abb");

        NFA nfa = new NFA(s0);
        nfa.addFinalState(s10);
        return nfa;
    }

    private NFA makeNFA2() {
        FSMState s0 = FSMState.makeState();
        FSMState s1 = FSMState.makeState();
        FSMState s2 = FSMState.makeState();
        FSMState s3 = FSMState.makeState();
        FSMState s4 = FSMState.makeState();
        FSMState s5 = FSMState.makeState();
        FSMState s6 = FSMState.makeState();
        FSMState s7 = FSMState.makeState();
        FSMState s8 = FSMState.makeState();

        s0.addTransition('\0', s1);
        s1.addTransition('a', s2);
        s0.addTransition('\0', s3);
        s3.addTransition('a', s4);
        s4.addTransition('b', s5);
        s5.addTransition('b', s6);
        s0.addTransition('\0', s7);
        s7.addTransition('a', s7);
        s7.addTransition('b', s8);
        s8.addTransition('b', s8);

        s2.setFinal(true);
        s6.setFinal(true);
        s8.setFinal(true);
        s2.addAcceptingRule("a");
        s6.addAcceptingRule("abb");
        s8.addAcceptingRule("a*b+");

        NFA nfa = new NFA(s0);
        nfa.addFinalState(s2);
        nfa.addFinalState(s6);
        nfa.addFinalState(s8);
        return nfa;
    }

    @Test
    public void testMatchString() throws IOException {
        Map<String, String> acceptingRules = new LinkedHashMap<>();
        acceptingRules.put("(a|b)*abb", "(a|b)*abb");
        lexer.setAcceptingRule(acceptingRules);

        NFA nfa = makeNFA1();
        nfa.draw(new File("graph/1-nfa.png"), 400);

        DFA dfa = new DFA(nfa);
        dfa.draw(new File("graph/1-dfa.png"), 400);

        assertEquals(new AbstractMap.SimpleEntry<>("", 1), dfa.match("abdsffgabb"));
        assertEquals(new AbstractMap.SimpleEntry<>("", 1), dfa.match("abab"));
        assertEquals(new AbstractMap.SimpleEntry<>("(a|b)*abb", 12), dfa.match("abbbababbabb"));
        assertEquals(new AbstractMap.SimpleEntry<>("(a|b)*abb", 3), dfa.match("abb"));
        assertEquals(new AbstractMap.SimpleEntry<>("(a|b)*abb", 6), dfa.match("abbabb"));
        assertEquals(new AbstractMap.SimpleEntry<>("(a|b)*abb", 4), dfa.match("aabbefg"));
    }

    @Test
    public void testMergeNFA() throws IOException {
        Map<String, String> acceptingRules = new LinkedHashMap<>();
        acceptingRules.put("a", "a");
        acceptingRules.put("abb", "abb");
        acceptingRules.put("a*b+", "a*b+");
        lexer.setAcceptingRule(acceptingRules);

        NFA nfa = makeNFA2();
        nfa.draw(new File("graph/2-nfa.png"), 400);

        DFA dfa = new DFA(nfa);
        dfa.draw(new File("graph/2-merged-nfa.png"), 400);

        assertEquals(new AbstractMap.SimpleEntry<>("abb", 3), dfa.match("abb"));
        assertEquals(new AbstractMap.SimpleEntry<>("a*b+", 4), dfa.match("abbb"));
        assertEquals(new AbstractMap.SimpleEntry<>("a", 1), dfa.match("aefg"));
        assertEquals(new AbstractMap.SimpleEntry<>("", 1), dfa.match("efg"));
    }
}

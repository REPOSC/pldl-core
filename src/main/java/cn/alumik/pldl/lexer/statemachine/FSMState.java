package cn.alumik.pldl.lexer.statemachine;

import cn.alumik.pldl.lexer.Lexer;
import cn.alumik.pldl.util.spring.AppContextUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class FSMState {

    private int id;

    private final Lexer lexer;

    private final Map<Character, Set<FSMState>> transitions = new HashMap<>();

    private boolean isFinal;

    private final List<String> acceptingRules = new ArrayList<>();

    public FSMState(Lexer lexer) {
        this.lexer = lexer;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public Map<Character, Set<FSMState>> getTransitions() {
        return transitions;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    List<String> getAcceptingRules() {
        return acceptingRules;
    }

    FSMState getNextStateOn(char by) {
        return getTransitions().get(by).iterator().next();
    }

    public void addTransition(char c, FSMState nextState) {
        if (!transitions.containsKey(c)) {
            transitions.put(c, new HashSet<>());
        }
        transitions.get(c).add(nextState);
    }

    public void addAcceptingRule(String acceptingRule) {
        acceptingRules.add(acceptingRule);
    }

    void addAcceptingRules(List<String> acceptingRules) {
        this.acceptingRules.addAll(acceptingRules);
    }

    void sortAcceptingRules() {
        List<String> acceptingRuleList = new ArrayList<>(lexer.getAcceptingRules().keySet());
        acceptingRules.sort(Comparator.comparingInt(acceptingRuleList::indexOf));
    }

    public static FSMState makeState() {
        return AppContextUtil.getBean(FSMState.class);
    }

    void dfs(Set<FSMState> states) {
        states.add(this);
        for (char c : transitions.keySet()) {
            for (FSMState nextState : transitions.get(c)) {
                if (!states.contains(nextState)) {
                    states.add(nextState);
                    nextState.dfs(states);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "状态" + id;
    }
}

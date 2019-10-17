package cn.alumik.pldl.lexer.statemachine;

import java.util.*;

public class DFA extends NFA {

    public DFA(NFA nfa) {
        Set<FSMState> startStateClosure = new HashSet<>();
        startStateClosure.add(nfa.getStartState());
        makeClosure(startStateClosure);

        Set<Set<FSMState>> stateClosureSet = new HashSet<>();
        stateClosureSet.add(startStateClosure);
        List<Set<FSMState>> stateClosureList = new ArrayList<>(stateClosureSet);
        Map<Set<FSMState>, FSMState> stateClosureToDFAState = new HashMap<>();

        FSMState newStartState = FSMState.makeState();
        stateClosureToDFAState.put(startStateClosure, newStartState);
        setStartState(newStartState);
        if (nfa.getStartState().isFinal()) {
            newStartState.setFinal(true);
            addFinalState(newStartState);
        }

        for (int i = 0; i < stateClosureList.size(); i++) {
            Set<FSMState> currentStateClosure = stateClosureList.get(i);
            Map<Character, Set<FSMState>> transitions = new HashMap<>();
            for (FSMState state : currentStateClosure) {
                Map<Character, Set<FSMState>> tmpTransitions = state.getTransitions();
                for (char c : tmpTransitions.keySet()) {
                    if (c != '\0') {
                        if (!transitions.containsKey(c)) {
                            transitions.put(c, new HashSet<>());
                        }
                        transitions.get(c).addAll(tmpTransitions.get(c));
                    }
                }
            }
            for (char c : transitions.keySet()) {
                Set<FSMState> nextStateClosure = transitions.get(c);
                makeClosure(nextStateClosure);
                if (!stateClosureSet.contains(nextStateClosure)) {
                    stateClosureSet.add(nextStateClosure);
                    stateClosureList.add(nextStateClosure);
                    FSMState newState = FSMState.makeState();
                    if (isFinalState(nextStateClosure)) {
                        newState.setFinal(true);
                        addFinalState(newState);
                        for (FSMState state : nextStateClosure) {
                            newState.addAcceptingRules(state.getAcceptingRules());
                        }
                        newState.sortAcceptingRules();
                    }
                    stateClosureToDFAState.put(nextStateClosure, newState);
                }
                stateClosureToDFAState
                        .get(currentStateClosure)
                        .addTransition(c, stateClosureToDFAState.get(nextStateClosure));
            }
        }
    }

    private static void makeClosure(Set<FSMState> stateSet) {
        List<FSMState> stateList = new ArrayList<>(stateSet);
        for (int i = 0; i < stateList.size(); i++) {
            Set<FSMState> nextStatesOnEpsilonTransitions = stateList.get(i).getTransitions().get('\0');
            if (nextStatesOnEpsilonTransitions != null) {
                for (FSMState state : nextStatesOnEpsilonTransitions) {
                    if (!stateSet.contains(state)) {
                        stateSet.add(state);
                        stateList.add(state);
                    }
                }
            }
        }
    }

    private static boolean isFinalState(Set<FSMState> stateClosure) {
        for (FSMState state : stateClosure) {
            if (state.isFinal()) {
                return true;
            }
        }
        return false;
    }

    public Map.Entry<String, Integer> match(String input) {
        FSMState currentState = getStartState();
        int index = 0;
        int finalIndex = 1;
        FSMState finalState = null;
        while (index < input.length()) {
            char c = input.charAt(index);
            if (currentState.getTransitions().containsKey(c)) {
                currentState = currentState.getNextStateOn(c);
                index++;
            } else {
                if (finalState != null && finalState.getAcceptingRules().size() > 0) {
                    return new AbstractMap.SimpleEntry<>(finalState.getAcceptingRules().get(0), finalIndex);
                }
                return new AbstractMap.SimpleEntry<>("", finalIndex);
            }
            if (currentState.isFinal()) {
                finalIndex = index;
                finalState = currentState;
            }
        }
        if (finalState != null && finalState.getAcceptingRules().size() > 0) {
            return new AbstractMap.SimpleEntry<>(finalState.getAcceptingRules().get(0), finalIndex);
        }
        return new AbstractMap.SimpleEntry<>("", finalIndex);
    }
}

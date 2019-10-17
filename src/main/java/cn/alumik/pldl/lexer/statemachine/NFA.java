package cn.alumik.pldl.lexer.statemachine;

import cn.alumik.pldl.util.Escape;
import cn.alumik.pldl.util.IdGenerator;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public class NFA {

    private FSMState startState;

    private Set<FSMState> finalStates = new HashSet<>();

    public NFA() {
        startState = FSMState.makeState();
        startState.setFinal(true);
        finalStates.add(startState);
    }

    public NFA(FSMState startState) {
        this.startState = startState;
        if (startState.isFinal()) {
            finalStates.add(startState);
        }
    }

    public FSMState getStartState() {
        return startState;
    }

    void setStartState(FSMState startState) {
        this.startState = startState;
    }

    public Set<FSMState> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(Set<FSMState> finalStates) {
        this.finalStates = finalStates;
    }

    public void addFinalState(FSMState state) {
        finalStates.add(state);
    }

    public void draw(File file) throws IOException {
        Graphviz.fromGraph(getGraph()).render(Format.PNG).toFile(file);
    }

    public void draw(File file, int height) throws IOException {
        Graphviz.fromGraph(getGraph()).height(height).render(Format.PNG).toFile(file);
    }

    private MutableGraph getGraph() {
        Graphviz.useEngine(new GraphvizV8Engine());
        MutableGraph graph = mutGraph(IdGenerator.next())
                .setDirected(true)
                .graphAttrs()
                .add(Rank.dir(LEFT_TO_RIGHT));
        Set<FSMState> states = new LinkedHashSet<>();
        startState.dfs(states);
        int id = 1;
        for (FSMState state : states) {
            state.setId(id++);
        }
        for (FSMState state : states) {
            MutableNode node = mutNode(String.valueOf(state.getId()));
            if (state.isFinal()) {
                node.add(Shape.DOUBLE_CIRCLE);
                MutableNode acceptingRuleNode = mutNode(state.getId() + " accepting rules")
                        .add(Label.lines(String.join("\n", state.getAcceptingRules())))
                        .add(Shape.RECTANGLE)
                        .add(Color.BLUE);
                node.addLink(
                        to(acceptingRuleNode)
                                .with(Style.DASHED)
                                .with(Arrow.NONE)
                                .with(Color.BLUE));
            } else {
                node.add(Shape.CIRCLE);
            }
            if (state == startState) {
                MutableNode entryNode = mutNode("0")
                        .add(Shape.NONE)
                        .add(Label.of(""))
                        .addLink(to(node).with(Label.of("start")));
                graph.add(entryNode);
            }
            for (char c : state.getTransitions().keySet()) {
                Set<FSMState> nextStates = state.getTransitions().get(c);
                for (FSMState nextState : nextStates) {
                    node.addLink(
                            to(mutNode(String.valueOf(nextState.getId())))
                                    .with(Label.of(" " + Escape.unescapeChar(c))));
                }
            }
            graph.add(node);
        }
        return graph;
    }
}

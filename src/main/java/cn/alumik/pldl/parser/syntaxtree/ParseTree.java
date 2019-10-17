package cn.alumik.pldl.parser.syntaxtree;

import cn.alumik.pldl.util.IdGenerator;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;

public class ParseTree {

    private ParseTreeNode root;

    public void setRoot(ParseTreeNode root) {
        this.root = root;
    }

    public void draw(File file) throws IOException {
        Graphviz.fromGraph(getGraph()).render(Format.PNG).toFile(file);
    }

    public void draw(File file, int height) throws IOException {
        Graphviz.fromGraph(getGraph()).height(height).render(Format.PNG).toFile(file);
    }

    private MutableGraph getGraph() {
        Graphviz.useEngine(new GraphvizV8Engine());
        MutableGraph graph = mutGraph(IdGenerator.next()).setDirected(true);

        List<MutableNode> graphNodes = new ArrayList<>();
        Queue<ParseTreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);

        while (!nodeQueue.isEmpty()) {
            ParseTreeNode currentNode = nodeQueue.poll();
            MutableNode currentGraphNode = mutNode(currentNode.getId())
                    .add(Label.of((String) currentNode.getSymbol().getProperties().get("value")));
            graphNodes.add(currentGraphNode);
            if (currentNode.getChildren().isEmpty()) {
                currentGraphNode.add(Color.BLUE);
            } else {
                for (ParseTreeNode nextNode : currentNode.getChildren()) {
                    nodeQueue.add(nextNode);
                    MutableNode nextGraphNode = mutNode(nextNode.getId());
                    currentGraphNode.addLink(nextGraphNode);
                }
            }
        }

        for (MutableNode graphNode : graphNodes) {
            graph.add(graphNode);
        }
        return graph;
    }
}

package cn.alumik.pldl.parser;

import cn.alumik.pldl.exception.ParsingException;
import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.parser.syntaxtree.ParseTree;
import cn.alumik.pldl.parser.syntaxtree.ParseTreeNode;
import cn.alumik.pldl.symbol.*;
import cn.alumik.pldl.util.yaml.ConfigLoader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
public class Parser {

    private final ConfigLoader configLoader;

    private ContextFreeGrammar grammar;

    public Parser(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public void init() throws AnalysisException {
        grammar = new ContextFreeGrammar(configLoader);
        grammar.initParseTable();
    }

    public ContextFreeGrammar getGrammar() {
        return grammar;
    }

    public ParseTree parse(List<TerminalSymbol> terminalSymbols) throws AnalysisException, ParsingException {
        List<Symbol> symbols = new ArrayList<>(terminalSymbols);
        ParseTree tree = new ParseTree();
        Stack<Integer> stateStack = new Stack<>();
        Stack<ParseTreeNode> nodeStack = new Stack<>();
        ParseTable parseTable = grammar.getParseTable();
        Map<Integer, Map<AbstractSymbol, Transition>> parseTableMap = parseTable.getTable();
        stateStack.push(0);
        int index = 0;
        AbstractSymbol startSymbol = parseTableMap
                .get(parseTable.getAcceptState())
                .get(grammar.getSymbolPool().getTerminalSymbol("$"))
                .getReduceProduction()
                .from();
        while (index != symbols.size() - 1 || !symbols.get(index).getAbstractSymbol().equals(startSymbol)) {
            int currentState = stateStack.peek();
            Symbol currentSymbol = index < symbols.size() ?
                    symbols.get(index) : new TerminalSymbol(grammar.getSymbolPool().getTerminalSymbol("$"));
            Transition transition = parseTableMap.get(currentState).get(currentSymbol.getAbstractSymbol());
            if (transition == null) {
                throw new ParsingException(
                        "程序分析到第 " + (index + 1) + " 个符号: " + currentSymbol + " 时既无法移进，也无法归约", null);
            } else {
                switch (transition.getOperation()) {
                    case Transition.SHIFT:
                        nodeStack.push(new ParseTreeNode(currentSymbol));
                    case Transition.GOTO:
                        stateStack.push(transition.getNextState());
                        index++;
                        break;
                    case Transition.REDUCE:
                        Production production = transition.getReduceProduction();
                        ParseTreeNode node = new ParseTreeNode(
                                new NonTerminalSymbol((AbstractNonTerminalSymbol) production.from()));
                        Stack<ParseTreeNode> tmpStack = new Stack<>();
                        for (AbstractSymbol ignored : production.to()) {
                            stateStack.pop();
                            tmpStack.push(nodeStack.pop());
                        }
                        for (AbstractSymbol ignored : production.to()) {
                            node.addChildren(tmpStack.pop());
                        }
                        nodeStack.push(node);
                        index--;
                        symbols.set(index, node.getSymbol());
                        break;
                }
            }
        }
        if (nodeStack.size() != 1) {
            throw new ParsingException("程序最终没有归约结束，符号栈中剩余: " + nodeStack, null);
        } else {
            tree.setRoot(nodeStack.pop());
        }
        return tree;
    }
}

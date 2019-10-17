package cn.alumik.pldl.parser;

import cn.alumik.pldl.exception.ParsingException;
import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.lexer.Lexer;
import cn.alumik.pldl.parser.syntaxtree.ParseTree;
import cn.alumik.pldl.symbol.AbstractNonTerminalSymbol;
import cn.alumik.pldl.util.yaml.ConfigLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParserTests {

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

    private void makeLexerAndParser(String config) throws AnalysisException, ParsingException {
        configLoader.loadConfig(config);
        parser.init();
        lexer.init();
    }

    @Test
    public void testInitFirstSets() throws AnalysisException, ParsingException {
        makeLexerAndParser("init-first-set-test.yml");

        for (AbstractNonTerminalSymbol symbol : parser.getGrammar().getSymbolPool().getNonTerminalSymbols()) {
            System.out.println(symbol);
            System.out.println(symbol.getFirstSet());
            System.out.println();
        }

        assertEquals("非终结符: A\n" +
                        "[终结符: a, 终结符: b, 终结符: c, 终结符: d, 终结符: g]\n" +
                        "\n" +
                        "非终结符: B\n" +
                        "[终结符: b, 终结符: null]\n" +
                        "\n" +
                        "非终结符: C\n" +
                        "[终结符: a, 终结符: c, 终结符: d]\n" +
                        "\n" +
                        "非终结符: D\n" +
                        "[终结符: d, 终结符: null]\n" +
                        "\n" +
                        "非终结符: _S\n" +
                        "[终结符: a, 终结符: b, 终结符: c, 终结符: d, 终结符: g]\n" +
                        "\n" +
                        "非终结符: E\n" +
                        "[终结符: c, 终结符: g]\n\n",
                outContent.toString().replace("\r", ""));
    }

    @Test
    public void watchParseTable() throws AnalysisException, ParsingException, IOException {
        makeLexerAndParser("parse-table-test.yml");
        lexer.lex("abababab");

        ParseTree parseTree = parser.parse(lexer.getResult());
        parseTree.draw(new File("graph/3-parse-tree.png"), 400);
    }

    @Test
    public void testParseCpp() throws AnalysisException, ParsingException {
        makeLexerAndParser("parse-cpp-test.yml");
        lexer.lex("int main() {\n" +
                "    int a = a + 1;\n" +
                "    cout << a << endl;\n" +
                "    return 0;\n" +
                "}");
        assertEquals("KEYWORD: int\n" +
                        "IDENTIFIER: main\n" +
                        "LP: (\n" +
                        "RP: )\n" +
                        "LB: {\n" +
                        "KEYWORD: int\n" +
                        "IDENTIFIER: a\n" +
                        "ASSIGN_OP: =\n" +
                        "IDENTIFIER: a\n" +
                        "ADD_OP: +\n" +
                        "INTEGER: 1\n" +
                        "SEMICOLON: ;\n" +
                        "IDENTIFIER: cout\n" +
                        "LSTREAM: <<\n" +
                        "IDENTIFIER: a\n" +
                        "LSTREAM: <<\n" +
                        "IDENTIFIER: endl\n" +
                        "SEMICOLON: ;\n" +
                        "KEYWORD: return\n" +
                        "INTEGER: 0\n" +
                        "SEMICOLON: ;\n" +
                        "RB: }\n",
                outContent.toString().replace("\r", ""));
    }
}

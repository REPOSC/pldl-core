package cn.alumik.pldl.util.yaml;

import cn.alumik.pldl.exception.ParsingException;
import cn.alumik.pldl.exception.AnalysisException;
import cn.alumik.pldl.lexer.Lexer;
import cn.alumik.pldl.parser.ContextFreeGrammar;
import cn.alumik.pldl.symbol.AbstractNonTerminalSymbol;
import cn.alumik.pldl.symbol.AbstractTerminalSymbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigLoaderTests {

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private Lexer lexer;

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

    @Test
    public void testConfigLoader() {
        configLoader.loadConfig("config-loader-tests.yml");

        System.out.println(configLoader.getNonTerminalSymbols());
        System.out.println(configLoader.getTerminalSymbols());
        System.out.println(configLoader.getAcceptingRules());
        System.out.println(configLoader.getIgnoredSymbols());
        System.out.println(configLoader.getStartSymbol());
        System.out.println(configLoader.getProductions());

        assertEquals(
                "[S, T, E, F]\n" +
                        "[IF]\n" +
                        "{NUMBER=[0-9]+, STRING=\".*\", IF=if}\n" +
                        "[STRING, NUMBER]\n" +
                        "S\n" +
                        "[S -> S T, S -> F, T -> T E]\n",
                outContent.toString().replace("\r", ""));
    }

    @Test
    public void testLoadLexRules() throws AnalysisException, ParsingException {
        configLoader.loadConfig("config-loader-tests.yml");
        lexer.init();

        assertEquals(
                Map.of("STRING", "\".*\"", "NUMBER", "[0-9]+", "IF", "if"),
                lexer.getAcceptingRules());
        assertEquals(
                Set.of(new String[]{"NUMBER", "STRING"}),
                lexer.getIgnoredSymbols());
    }

    @Test
    public void testLoadParseRules() throws AnalysisException {
        configLoader.loadConfig("config-loader-tests.yml");

        ContextFreeGrammar grammar = new ContextFreeGrammar(configLoader);
        System.out.println(grammar);
        assertEquals("上下文无关文法包含以下产生式:\n" +
                        "产生式: 非终结符: _S -> 非终结符: S\n" +
                        "产生式: 非终结符: S -> 非终结符: S 非终结符: T\n" +
                        "产生式: 非终结符: S -> 非终结符: F\n" +
                        "产生式: 非终结符: T -> 非终结符: T 非终结符: E\n",
                outContent.toString().replace("\r", ""));
        assertEquals(
                Set.of(new AbstractTerminalSymbol[]{
                        new AbstractTerminalSymbol("null"),
                        new AbstractTerminalSymbol("IF"),
                        AbstractTerminalSymbol.End()
                }),
                grammar.getSymbolPool().getTerminalSymbols());
        assertEquals(
                Set.of(new AbstractNonTerminalSymbol[]{
                        new AbstractNonTerminalSymbol("S"),
                        new AbstractNonTerminalSymbol("_S"),
                        new AbstractNonTerminalSymbol("E"),
                        new AbstractNonTerminalSymbol("F"),
                        new AbstractNonTerminalSymbol("T")
                }),
                grammar.getSymbolPool().getNonTerminalSymbols());
    }
}

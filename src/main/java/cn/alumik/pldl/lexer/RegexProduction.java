package cn.alumik.pldl.lexer;

import cn.alumik.pldl.lexer.statemachine.NFA;
import cn.alumik.pldl.symbol.Symbol;
import cn.alumik.pldl.parser.Production;

import java.util.List;

abstract class RegexProduction extends Production {

    RegexProduction(Production production) {
        super(production);
    }

    public abstract NFA getNFA(List<NFA> nodes, List<Symbol> children);
}

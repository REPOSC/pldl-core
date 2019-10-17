package cn.alumik.pldl.util.yaml;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.*;

@Component
public class ConfigLoader {

    private Config config;

    public void loadConfig(String configPath) {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(configPath);
        config = yaml.load(inputStream);
    }

    public Set<String> getNonTerminalSymbols() {
        return config.getNonTerminalSymbols();
    }

    public Set<String> getTerminalSymbols() {
        Set<String> terminalSymbols = new HashSet<>(config.getTerminalSymbols().keySet());
        terminalSymbols.removeAll(config.getIgnoredSymbols());
        return terminalSymbols;
    }

    public Map<String, String> getAcceptingRules() {
        return config.getTerminalSymbols();
    }

    public Set<String> getIgnoredSymbols() {
        return config.getIgnoredSymbols();
    }

    public String getStartSymbol() {
        return config.getStartSymbol();
    }

    public List<String> getProductions() {
        return config.getProductions();
    }
}

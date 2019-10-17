这是用户[Alumik](https://github.com/alumik)创建的项目，下面是原始的readme：

# PLDL(Programming Language Description Language)

PLDL 是编程语言描述语言的简称。

本项目在 [PLDLParser](https://github.com/LLyronx/PLDLParser) 的基础上修改。

## 开发路线图

### File Reader

- [ ] Source file reader
- [x] YAML config reader

### Lexer

- [x] Regular expression engine
    - [x] Regular expression to NFA
    - [x] Finite-state machine
    - [x] State machine visualization
- [x] Lex rules initialization
- [x] Lexer core features

### Parser

- [x] Parse rules initialization
- [x] Symbol and symbol pool
- [x] Context-free grammar
- [x] Parse table
- [x] Production rules
- [x] Parser core features
- [x] Build parse tree

## 词法分析

### NFA 转 DFA 示例

以正则表达式 (a|b)\*abb 为例。

#### 构建 NFA

![KPTaGR.png](https://s2.ax1x.com/2019/10/16/KPTaGR.png)
    
#### 转换为 DFA

![KPTtIJ.png](https://s2.ax1x.com/2019/10/16/KPTtIJ.png)

### 多个 NFA 合并示例

#### 由三个正则表达式构建三个 NFA 并合并为一个 NFA

![KPTYa4.png](https://s2.ax1x.com/2019/10/16/KPTYa4.png)

#### 转换为 DFA

![KPTUi9.png](https://s2.ax1x.com/2019/10/16/KPTUi9.png)

## 语法分析

### 建立语法分析树示例

#### 现有文法

1. _S->S （该产生式由文法增广自动产生，不必书写进配置文件）
2. S->CbBA
3. A->Aab
4. A->ab
5. B->C
6. B->Db
7. C->a
8. D->a

#### 编写 PLDL 配置文件

```yaml
nonTerminalSymbols:
  ? A
  ? B
  ? C
  ? D
  ? S
terminalSymbols:
  a: a
  b: b
ignoredSymbols:
startSymbol: S
productions:
  - S -> C b B A
  - A -> A a b
  - A -> a b
  - B -> C
  - B -> D b
  - C -> a
  - D -> a
```

#### 建立分析表

<table>
  <tr>
    <th></th>
    <th colspan="3">ACTION</th>
    <th colspan="5">GOTO</th>
  </tr>
  <tr>
    <td></td>
    <td>a</td>
    <td>b</td>
    <td>$</td>
    <td>S</td>
    <td>A</td>
    <td>B</td>
    <td>C</td>
    <td>D</td>
  </tr>
  <tr>
    <td>0</td>
    <td>s1</td>
    <td></td>
    <td></td>
    <td>3</td>
    <td></td>
    <td></td>
    <td>2</td>
    <td></td>
  </tr>
  <tr>
    <td>1</td>
    <td></td>
    <td>r7</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>2</td>
    <td></td>
    <td>s4</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>3</td>
    <td></td>
    <td></td>
    <td>acc</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>4</td>
    <td>s5</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td>6</td>
    <td>7</td>
    <td>8</td>
  </tr>
  <tr>
    <td>5</td>
    <td>r7</td>
    <td>r8</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>6</td>
    <td>s10</td>
    <td></td>
    <td></td>
    <td></td>
    <td>9</td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>7</td>
    <td>r5</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>8</td>
    <td></td>
    <td>s11</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>9</td>
    <td>s12</td>
    <td></td>
    <td>r2</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>10</td>
    <td></td>
    <td>s13</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>11</td>
    <td>r6</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>12</td>
    <td></td>
    <td>s14</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>13</td>
    <td>r4</td>
    <td></td>
    <td>r4</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>14</td>
    <td>r3</td>
    <td></td>
    <td>r3</td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
</table>

#### 建立语法分析树

输入串为 abababab 。

![KC59Yt.png](https://s2.ax1x.com/2019/10/15/KC59Yt.png)

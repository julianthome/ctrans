# CTrans
Basic **C**onstraint **Trans**formation library

# Status

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)][licence]
[![Language](http://img.shields.io/badge/language-java-brightgreen.svg)][language]
[![Maven](https://maven-badges.herokuapp.com/maven-central/com.github.julianthome/ctrans/badge.svg)][maven]
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.github.julianthome/ctrans/badge.svg)][javadoc]
[![Linux Build Status](https://img.shields.io/travis/julianthome/ctrans/master.svg?label=Linux%20build)][travis]
[![Windows Build status](https://img.shields.io/appveyor/ci/julianthome/ctrans/master.svg?label=Windows%20build)][appveyor]
[![Test Coverage](https://codecov.io/gh/julianthome/ctrans/branch/master/graph/badge.svg)][coverage]

[licence]: https://opensource.org/licenses/mit
[language]: https://www.java.com
[maven]: https://maven-badges.herokuapp.com/maven-central/com.github.julianthome/ctrans
[javadoc]: http://www.javadoc.io/doc/com.github.julianthome/ctrans
[travis]: https://travis-ci.org/julianthome/ctrans
[appveyor]: https://ci.appveyor.com/project/julianthome/ctrans
[coverage]: https://codecov.io/gh/julianthome/ctrans


# Background
CTrans supports basic transformation rules as explained in [these
lecture notes](http://resources.mpi-inf.mpg.de/departments/rg1/teaching/autrea-ss10/script/lecture2.pdf). 
A more comprehensive description can be also found
[here](http://www.mpi-inf.mpg.de/~weidenb/publications/handbook99small.ps.gz).

# Usage

CTrans is a tool for translating a given boolean expression into CNF or DNF,
respectively. The boolean expression has to satisfy the following context-free
grammar definition.

```antlr
grammar Logic;

rule_set : expression EOF ;

conclusion : IDENTIFIER ;

expression
 : not expression
 | expression and expression
 | expression or expression
 | expression xor expression
 | expression implies expression
 | LPAREN expression RPAREN
 | atom;

atom : IDENTIFIER
| LPAREN IDENTIFIER RPAREN;

not : 'not';
and : 'and' ;
or : 'or' ;
xor : 'xor';
implies : 'implies';

LPAREN : '(' ;
RPAREN : ')' ;

IDENTIFIER : [a-zA-Z_][a-zA-Z_0-9]*;

WS : [ \r\t\n]+ -> skip ;
```

The following example illustrates how CTrans can be used in order to translate
a given boolean expression `a implies d`. First, we invoke the method
`translate()` on the static class `CTrans` that takes as first input the
expression that we wish to translate, and as second argument the target format
which can be either `DNF` or `CNF`.  The call to `translate()` will return an
`Ast` object which is the abstract syntax tree (AST) of translated input
expression. In the example below `a` is the DNF representation of `a implies b`.

```java
Ast a = CTrans.INSTANCE.translate("a implies d", TranslationTarget.DNF);
System.out.println(a.toDot());
```

For visualizing the abstract syntax tree, the method `toDot()` can be invoked
on the `Ast` object which will return a String in `dot` format; the AST can
then be visualized by means of [graphviz](http://www.graphviz.org).  For the
visualization of the dot files [this script](https://gist.github.com/julianthome/66a31203b9b25493fa2a43889f948212)
might be helpful. For the example expression, the following AST will be
generated:

![](https://www.dropbox.com/s/58jm1992nddv13i/dnf.png?dl=1)

# Licence

The MIT License (MIT)

Copyright (c) 2017 Julian Thome <julian.thome.de@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


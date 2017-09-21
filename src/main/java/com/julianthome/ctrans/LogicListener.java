/**
 * CTrans - A constraint translator
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2017 Julian Thome <julian.thome.de@gmail.com>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package com.julianthome.ctrans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.inmemantlr.exceptions.ParseTreeProcessorException;
import org.snt.inmemantlr.tree.ParseTree;
import org.snt.inmemantlr.tree.ParseTreeNode;
import org.snt.inmemantlr.tree.ParseTreeProcessor;


public class LogicListener extends
        ParseTreeProcessor<ExpressionGraph, Expression> {

    final static Logger LOGGER = LoggerFactory.getLogger(LogicListener.class);


    private ExpressionGraph eg = new ExpressionGraph();
    /**
     * constructor
     *
     * @param ast abstract syntax tree to process
     */
    public LogicListener(ParseTree ast) {
        super(ast);
        LOGGER.debug(ast.toDot());
    }

    @Override
    public ExpressionGraph getResult() {
        return eg;
    }


    @Override
    protected void initialize() {

    }

    @Override
    protected void process(ParseTreeNode n) throws ParseTreeProcessorException {
        LOGGER.debug("ID " + n.getId() + " " + n.getRule() + " " + n.getLabel());

        switch (n.getRule()) {
            case "expression":

                if (n.getChildren().size() == 3) {
                    // we have a boolean expression

                    assert smap.containsKey(n.getChild(0));
                    assert smap.containsKey(n.getChild(2));

                    Expression c1 = smap.get(0);
                    Expression c2 = smap.get(2);
                    Expression.Kind kind = Expression.Kind.ATOM;

                    switch (n.getChild(1).getLabel()) {
                        case "and":
                            kind = Expression.Kind.AND;
                            break;
                        case "or":
                            kind = Expression.Kind.OR;
                            break;
                        case "xor":
                            kind = Expression.Kind.XOR;
                            break;
                        case "implies":
                            kind = Expression.Kind.IMPLIES;
                            break;
                    }
                    Expression op = eg.addExpression(kind, smap.get(n.getChild
                            (0)), smap.get(n.getChild(2)));

                    smap.put(n, op);
                } else if (n.getChildren().size() == 2) {
                    assert smap.containsKey(n.getChild(1));

                    Expression.Kind kind = Expression.Kind.ATOM;

                    switch(n.getChild(0).getLabel()) {
                        case "not":
                            kind = Expression.Kind.NEGATION;
                            break;
                    }

                    Expression op = eg.addExpression(kind, smap.get(n.getChild
                            (1)));

                    smap.put(n, op);
                } else {
                    assert n.getChildren().size() == 1;
                    simpleProp(n);
                }
                break;
            case "atom":
                Expression exp = new Expression(n.getLabel());
                eg.addVertex(exp);
                smap.put(n, exp);
                break;
            case "s":
                simpleProp(n);
                break;


        }

    }
}

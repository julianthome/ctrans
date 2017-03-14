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


package com.julianthome.ctrans.translator;

import com.julianthome.ctrans.Edge;
import com.julianthome.ctrans.Expression;
import com.julianthome.ctrans.ExpressionGraph;
import com.julianthome.ctrans.TranslationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


public class ImplicationTranslator extends TranslationHandler {

    final static Logger LOGGER = LoggerFactory.getLogger(ImplicationTranslator.class);

    @Override
    public boolean isActive(ExpressionGraph eg, Expression e) {
        return e.getKind() == Expression.Kind.IMPLIES;
    }

    @Override
    public void translate(ExpressionGraph eg, Expression impl) {
        List<Expression> ex = eg.getParamtersFor(impl);
        Set<Edge> outgoing = eg.outgoingEdgesOf(impl);

        LOGGER.debug("translate conjunction");

        assert ex.size() == 2;

        Expression ex0 = ex.get(0);
        Expression ex1 = ex.get(1);

        Expression np0 = eg.addExpression(Expression.Kind.NEGATION, ex0);
        Expression or = eg.addExpression(Expression.Kind.OR, np0, ex1);


        for(Edge e :outgoing) {
            eg.addEdge(or, e.getTarget(), e.getSequence());
        }

        eg.removeVertex(impl);
    }
}

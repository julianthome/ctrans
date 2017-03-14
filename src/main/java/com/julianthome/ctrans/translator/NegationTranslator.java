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

import com.julianthome.ctrans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NegationTranslator extends TranslationHandler {

    final static Logger LOGGER = LoggerFactory.getLogger(NegationTranslator.class);


    @Override
    public boolean isActive(ExpressionGraph eg, Expression negation) {


        if(negation.getKind() != Expression.Kind.NEGATION)
            return false;

        List<Expression> ex = eg.getParamtersFor(negation);
        Expression param = ex.get(0);

        return param.getKind() != Expression.Kind.ATOM;
    }

    @Override
    public void translate(ExpressionGraph eg, Expression negation) {


        List<Expression> ex = eg.getParamtersFor(negation);
        Set<Edge> outgoing = eg.outgoingEdgesOf(negation);

        LOGGER.debug("translate negation");

        assert ex.size() == 1;

        Expression param = ex.get(0);

        if(param.getKind() == Expression.Kind.ATOM)
            return;

        Set<Edge> toAdd = new HashSet<>();

        if(param.getKind() == Expression.Kind.NEGATION) {
            // Double negation

            List<Expression> parin = eg.getParamtersFor(param);

                for (Expression pin : parin) {
                    for(Edge o : outgoing) {
                        LOGGER.debug("add edge {} {}" +  pin +  o.getTarget());
                        toAdd.add(new Edge(pin,o.getTarget(),o.getSequence()));
                    }
                }

            eg.removeVertex(param);

        } else if (param.getKind() == Expression.Kind.AND) {
            // push negation downward
            pushAndOr(param, eg, Expression.Kind.OR);

            for(Edge o : outgoing) {
                toAdd.add(new Edge(param,o.getTarget(),o.getSequence()));
            }


        } else if (param.getKind() == Expression.Kind.OR) {
            // push negation downward
            pushAndOr(param, eg, Expression.Kind.AND);

            for(Edge o : outgoing) {
                toAdd.add(new Edge(param,o.getTarget(),o.getSequence()));
            }
        }
        for(Edge add : toAdd ){
            LOGGER.debug("src {}" +  add.getSource());
            eg.addEdge(add.getSource(), add.getTarget(),add);
        }


        eg.removeVertex(negation);
    }

    private void pushAndOr(Expression param, ExpressionGraph eg, Expression
            .Kind changeTo) {
        // push negation downward
        Expression and = param;
        List<Expression> andpars = eg.getParamtersFor(and);

        assert andpars.size() == 2;

        Expression first = andpars.get(0);
        Expression second = andpars.get(1);

        // remove parameter edges
        eg.removeEdge(first, and);
        eg.removeEdge(second,and);


        Expression nfirst = eg.addExpression(Expression.Kind.NEGATION,
                first);

        Expression nsecond = eg.addExpression(Expression.Kind.NEGATION,
                second);

        eg.addEdge(nfirst, and,0);
        eg.addEdge(nsecond, and, 1);

        and.setKind(changeTo);

    }
}

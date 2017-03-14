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

import java.util.*;

import static com.julianthome.ctrans.Expression.Kind.*;


// Push disjunctions downwards
public class ConjunctionTranslator extends TranslationHandler {


    final static Logger LOGGER = LoggerFactory.getLogger(ConjunctionTranslator.class);

    @Override
    public boolean isActive(ExpressionGraph eg, Expression e) {

        LOGGER.debug("is conjunciton active");

        if(e.getKind() == AND) {
            List<Expression> ex = eg.getParamtersFor(e);

            return ex.stream().filter(x -> x.getKind() == OR)
                    .count() > 0;
        }
        return false;
    }

    @Override
    public void translate(ExpressionGraph eg, Expression and) {
        // push conjuntions downward

        List<Expression> ex = eg.getParamtersFor(and);

        Set<Edge> out = eg.outgoingEdgesOf(and);

        assert ex.size() == 2;

        Expression ex0 = ex.get(0);
        Expression ex1 = ex.get(1);

        List<Expression> ex0par = new Vector<>();
        List<Expression> ex1par = new Vector<>();
        Set<Expression> toDel = new HashSet<>();

        if(eg.hasParameters(ex0) && ex0.getKind() == OR) {
            ex0par = eg.getParamtersFor(ex0);
            assert (ex0.getKind() == OR);
            toDel.add(ex0);
        } else {
            LOGGER.debug("PAR 0" + ex0.getKind().toString());
            assert ex0.getKind() == ATOM || ex0.getKind() == AND ||
                    ex0.getKind() == NEGATION;
            ex0par.add(ex0);
        }


        if(eg.hasParameters(ex1) && ex1.getKind() == OR) {
            ex1par = eg.getParamtersFor(ex1);
            assert (ex1.getKind() == OR);
            toDel.add(ex1);
        } else {
            LOGGER.debug("PAR 1" + ex1.getKind().toString());
            assert ex1.getKind() == ATOM || ex1.getKind() == AND ||
                    ex1.getKind() == NEGATION;
            ex1par.add(ex1);
        }

        Expression nex = null;

        for(Expression par0 : ex0par) {
            for(Expression par1 : ex1par){
                LOGGER.debug("link {} and {}" +  par0.getId() + par1.getId());
                Expression tmp = eg.addExpression(AND, par0, par1);
                if(nex == null)
                    nex = tmp;
                else
                    nex = eg.addExpression(OR, nex, tmp);
                
            }
        }

        for(Edge o : out) {
            eg.addEdge(nex, o.getTarget(),o.getSequence());
        }

        toDel.add(and);

        eg.removeAllVertices(toDel);

        check(eg);
    }

    private void check(ExpressionGraph eg) {

        for(Expression e : eg.vertexSet()) {
            if(e.getKind() == OR) {
                LOGGER.debug("eg {}" + e.getId());
                assert eg.getParamtersFor(e).size() == 2;
            }
        }
    }

}

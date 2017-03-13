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

import java.util.List;
import java.util.Queue;
import java.util.Set;


public class XorTranslator extends TranslationHandler {

    final static Logger LOGGER = LoggerFactory.getLogger(XorTranslator.class);

    @Override
    public boolean isActive(ExpressionGraph eg, Expression e) {
        return e.getKind() == Expression.Kind.XOR;
    }

    @Override
    public void translate(ExpressionGraph eg, Expression xor, Queue<Expression> todolist) {
        List<Expression> ex = eg.getParamtersFor(xor);
        Set<Edge> outgoing = eg.outgoingEdgesOf(xor);

        LOGGER.debug("translate xor");

        assert ex.size() == 2;

        Expression ex0 = ex.get(0);
        Expression ex1 = ex.get(1);

        Expression first = eg.addExpression(Expression.Kind.OR,ex0,ex1);
        Expression nex0 = eg.addExpression(Expression.Kind.NEGATION,ex0);
        Expression nex1 = eg.addExpression(Expression.Kind.NEGATION,ex1);
        Expression second = eg.addExpression(Expression.Kind.OR,nex0,nex1);

        Expression nand = eg.addExpression(Expression.Kind.AND, first, second);

        eg.removeVertex(xor);

        for(Edge out : outgoing){
            eg.addEdge(nand, out.getTarget(),out.getSequence());
        }

        addToTodoList(todolist, ex0, ex1, first, nex0, nex1, second, nand);
    }
}

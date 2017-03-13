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

import org.jgrapht.graph.DirectedPseudograph;

import java.util.*;
import java.util.stream.Collectors;

public class ExpressionGraph extends DirectedPseudograph<Expression,Edge> {


    public ExpressionGraph() {
        super(Edge.class);
    }

    public boolean hasParameters(Expression ex) {
        return getParamtersFor(ex).size() > 0;
    }

    public List<Expression> getParamtersFor(Expression ex) {
        List<Expression>  ret = new Vector<>();
        TreeSet<Edge> sort = new TreeSet<>();
        sort.addAll(incomingEdgesOf(ex));
        for(Edge s : sort) {
            ret.add(s.getSource());
        }
        return ret;
    }

    public Expression addExpression(Expression.Kind kind, Expression ex1,
                              Expression ex2) {
        Expression exp = new Expression(kind);
        addVertex(exp);
        addVertex(ex1);
        addVertex(ex2);
        addEdge(ex1, exp, new Edge(ex1, exp,1));
        addEdge(ex2, exp, new Edge(ex2, exp,2));
        return exp;
    }

    public Expression addExpression(Expression.Kind kind, Expression ex1) {
        Expression exp = new Expression(kind);
        addVertex(exp);
        addVertex(ex1);
        addEdge(ex1, exp, new Edge(ex1, exp,1));
        return exp;
    }

    public Expression replace (Expression a, Expression b) {


        Set<Edge> ain = incomingEdgesOf(a);
        Set<Edge> aout = outgoingEdgesOf(a);
        Set<Edge> toadd = new HashSet<>();

        for(Edge i : ain) {
            toadd.add(new Edge(i.getSource(),b,i.getSequence()));
        }

        for(Edge o : aout) {
            toadd.add(new Edge(b,o.getTarget(),o.getSequence()));
        }

        addVertex(b);
        removeVertex(a);
        toadd.forEach(t -> addEdge(t.getSource(),t.getTarget(),t.getSequence
                ()));

        return b;
    }


    public void addEdge(Expression src, Expression dst, int seq){
        super.addEdge(src, dst, new Edge(src,dst,seq));
    }


    public Set<Expression> connectedInNodes(Expression e) {
        return incomingEdgesOf(e).stream().map(x -> x.getSource()).collect
                (Collectors.toSet());
    }

    public Set<Expression> connectedOutNodes(Expression e) {
        return outgoingEdgesOf(e).stream().map(x -> x.getTarget()).collect
                (Collectors.toSet());
    }

    public String toDot() {

        StringBuilder sb = new StringBuilder();

        sb.append("digraph {\n" +
                "\trankdir=TB;\n");

        sb.append("\tnode [fontname=Helvetica,fontsize=11];\n");
        sb.append("\tedge [fontname=Helvetica,fontsize=10];\n");

        String shape = "";
        String label = "";
        String color = "black";


        for (Expression n : this.vertexSet()) {
            String kind = "";

            if(n.getKind() == Expression.Kind.ATOM) {
                shape = "ellipse";
            } else {
                shape = "box";
            }

            sb.append("\tn" + n.getId() + " [color=" + color + ",shape=\"" +
                    shape + "\"," + "label=\"" + n.getId() +"\\n" + n
                    .toString()
                    +"\"];\n");
        }

        String option = "";
        String ecolor = "black";
        String par = "";

        for (Edge e : this.edgeSet()) {
            Expression src = e.getSource();
            Expression dest = e.getTarget();

            assert (outgoingEdgesOf(src).contains(e));
            assert (incomingEdgesOf(dest).contains(e));

            assert (src != null);
            assert (dest != null);


            sb.append("\tn" + src.getId() + " -> n" + dest.getId() +
                    "[color=\"" + ecolor + "\",label=\"" + e.sequence + "\"" +
                    option + "];\n");

        }

        sb.append("}");
        return sb.toString();
    }


    private String buildStringsForExp(Map<Expression,String> smap,
                                          Expression ex) {

        if(smap.containsKey(ex))
            return smap.get(ex);

        if(ex.getKind() == Expression.Kind.ATOM) {
            smap.put(ex, ex.toString());
            return ex.toString();
        } else if (ex.getKind() == Expression.Kind.NEGATION) {
            Expression par0 = getParamtersFor(ex).get(0);
            smap.put(ex, "not " + buildStringsForExp(smap,par0));
        } else {
            // any binary operation
            assert getParamtersFor(ex).size() == 2;

            Expression par0 = getParamtersFor(ex).get(0);

            Expression par1 = getParamtersFor(ex).get(1);

            smap.put(ex, buildStringsForExp(smap,par0) + " " + ex.toString()
                    + " " +
                    buildStringsForExp(smap,par1));
        }

        return smap.get(ex);

    }


    public String serialize() {

        Map<Expression,String> emap = new HashMap<>();
        for(Expression ex : vertexSet()) {
            buildStringsForExp(emap, ex);
        }

        Set<Expression> root = vertexSet().stream().filter(x -> outDegreeOf
                (x) == 0).collect(Collectors.toSet());

        assert root.size() == 1;

        assert emap.containsKey(root.iterator().next());
        return emap.get(root.iterator().next());

    }


}

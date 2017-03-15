package com.julianthome.ctrans.translator;

import com.julianthome.ctrans.Edge;
import com.julianthome.ctrans.Expression;
import com.julianthome.ctrans.ExpressionGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by julian on 15/03/2017.
 */
public enum ConDisjunctionUtils {
    INSTANCE;

    final static Logger LOGGER = LoggerFactory.getLogger(ConDisjunctionUtils.class);

    public List<Expression> getParToCheck(ExpressionGraph eg, Expression e,
                                          Set<Expression> toDel,
                                          Expression.Kind searchKind) {
        List<Expression> expar = new Vector<>();


        if (eg.hasParameters(e) && e.getKind() == searchKind) {
            expar = eg.getParamtersFor(e);
            assert (e.getKind() == searchKind);
            toDel.add(e);
        } else {
            LOGGER.debug("PAR 0" + e.getKind().toString());
            expar.add(e);
        }
        return expar;
    }

    public Expression link(ExpressionGraph eg, List<Expression> a,
                           List<Expression> b, Expression.Kind inner,
                           Expression.Kind outer) {
        Expression nex = null;
        for (Expression par0 : a) {
            for (Expression par1 : b) {
                LOGGER.debug("link {} and {}" + par0.getId() + par1.getId());
                Expression tmp = eg.addExpression(inner, par0, par1);
                if (nex == null)
                    nex = tmp;
                else
                    nex = eg.addExpression(outer, nex, tmp);
            }
        }
        return nex;
    }

    public void translate(ExpressionGraph eg, Expression e, Expression.Kind
            search, Expression.Kind inner, Expression.Kind outer) {
        // push conjuntions downward

        List<Expression> ex = eg.getParamtersFor(e);

        Set<Edge> out = eg.outgoingEdgesOf(e);

        assert ex.size() == 2;

        Expression ex0 = ex.get(0);
        Expression ex1 = ex.get(1);

        Set<Expression> toDel = new HashSet<>();
        List<Expression> ex0par = ConDisjunctionUtils.INSTANCE.getParToCheck
                (eg, ex0, toDel, search);
        List<Expression> ex1par = ConDisjunctionUtils.INSTANCE.getParToCheck
                (eg, ex1, toDel, search);

        Expression nex = ConDisjunctionUtils.INSTANCE.link(eg, ex0par,
                ex1par, inner, outer);

        for(Edge o : out) {
            eg.addEdge(nex, o.getTarget(),o.getSequence());
        }

        toDel.add(e);

        eg.removeAllVertices(toDel);
    }

}

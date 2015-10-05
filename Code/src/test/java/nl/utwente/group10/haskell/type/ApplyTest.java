package nl.utwente.group10.haskell.type;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableSet;

import nl.utwente.group10.haskell.env.Environment;
import nl.utwente.group10.haskell.env.HaskellCatalog;
import nl.utwente.group10.haskell.exceptions.CatalogException;
import nl.utwente.group10.haskell.exceptions.HaskellException;
import nl.utwente.group10.haskell.expr.Apply;
import nl.utwente.group10.haskell.expr.Expression;
import nl.utwente.group10.haskell.expr.Hole;
import nl.utwente.group10.haskell.expr.Ident;
import nl.utwente.group10.haskell.expr.Value;

import org.junit.Test;

public class ApplyTest {
    @Test
    public void testApplyPlus() throws CatalogException, HaskellException {
        Environment env = new HaskellCatalog().asEnvironment();
        
        Expression e0 = new Ident("(+)");
        Type t0 = e0.findType(env);
        assertEquals("(Num a) -> (Num a) -> (Num a)", t0.toHaskellType());
        
        Expression e1 = new Value(Type.con("Float"), "5.0");
        Type t1 = e1.findType(env);
        assertEquals("Float", t1.toHaskellType());
        
        Expression e2 = new Apply(e0, e1);
        Type t2 = e2.findType(env);
        assertEquals("Float -> Float", t2.toHaskellType());
        
        Expression e3 = new Value(Type.con("Float"), "5.0");
        Type t3 = e3.findType(env);
        assertEquals("Float", t3.toHaskellType());
        
        Expression e4 = new Apply(e2, e3);
        Type t4 = e4.findType(env);
        assertEquals("Float", t4.toHaskellType());
    }
    
    @Test
    public void testApplyUndefined() throws CatalogException, HaskellException {
        Environment env = new HaskellCatalog().asEnvironment();
        
        Expression e0 = new Ident("(+)");
        Type t0 = e0.findType(env);
        assertEquals("(Num a) -> (Num a) -> (Num a)", t0.toHaskellType());
        
        Expression e1 = new Hole();
        Type t1 = e1.findType(env);
        TypeChecker.unify(t1, Type.con("Float"));
        // t1 Should unfiy with everything (the type of t1 should be 'a').
        // No exception thrown -> Types are the same, as expected. The test will
        // fail if an Exception is thrown.
        
        Expression e2 = new Apply(e0, e1);
        Type t2 = e2.findType(env);
        Type num = TypeChecker.makeVariable("n", ImmutableSet.of(env.lookupClass("Num")));
        TypeChecker.unify(t2, Type.fun(num, num));
        
        Expression e3 = new Apply(e2, new Hole());
        Type t3 = e3.findType(env);
        TypeChecker.unify(t3, Type.con("Float"));
        // t3 Should unfiy with everything (the type of t3 should be 'a').
        // No exception thrown -> Types are the same, as expected. The test will
        // fail if an Exception is thrown.
    }
}

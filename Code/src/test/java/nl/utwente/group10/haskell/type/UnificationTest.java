package nl.utwente.group10.haskell.type;

import static org.junit.Assert.*;

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

public class UnificationTest {

    @Test
    public void testUnifyUndefined() throws CatalogException, HaskellException {
        Environment env = new HaskellCatalog().asEnvironment();

        Expression e0 = new Ident("const");
        Type t0 = e0.findType(env);
        assertEquals("a -> b -> a", t0.toHaskellType());

        Expression e1 = new Apply(e0, new Hole());
        Type t1 = e1.findType(env);
        assertEquals("b -> a", t1.toHaskellType());

        Expression e2 = new Apply(e1, new Hole());
        Type t2 = e2.findType(env);

        TypeChecker.unify(t2, new Hole().findType(env));
        //No exception thrown -> Types are the same, as expected. The test will fail if an Exception is thrown.
    }

    @Test
    public void testUnifyFloats() throws CatalogException, HaskellException {
        Environment env = new HaskellCatalog().asEnvironment();
        
        Expression e0 = new Ident("const");
        Type t0 = e0.findType(env);
        assertEquals("a -> b -> a", t0.toHaskellType());

        Expression e1 = new Apply(e0, new Value(Type.con("Float"), "5.0"));
        Type t1 = e1.findType(env);
        assertEquals("b -> Float", t1.toHaskellType());

        Expression e2 = new Apply(e1, new Value(Type.con("Float"), "5.0"));
        Type t2 = e2.findType(env);
        assertEquals("Float", t2.toHaskellType());
    }
    
    @Test
    public void testUnifyABool() throws HaskellException{
        Environment env = new HaskellCatalog().asEnvironment();

        Expression e0 = new Ident("const");
        Type t0 = e0.findType(env);

        Expression e1 = new Apply(e0, new Hole());
        Type t1 = e1.findType(env);

        Expression e2 = new Apply(e1, new Hole());
        Type t2 = e2.findType(env);
        
        Type t3 = TypeChecker.makeVariable("t");
        Type t4 = Type.con("Bool");
        
        TypeChecker.unify(t3, t4);
        
        TypeChecker.unify(t2, t4);
    }

    @Test
    public void testTypeclassCopy() throws HaskellException {
        Environment env = new HaskellCatalog().asEnvironment();
        TypeClass num = env.lookupClass("Num");
        TypeClass read = env.lookupClass("Show");
        TypeClass show = env.lookupClass("Read");

        Type ct = Type.con("Float");
        Type t0 = Type.var("a", num, read);
        Type t1 = Type.var("b", num, show);

        Expression e0 = new Value(t0, "?");
        Expression e1 = new Value(t1, "?");
        Expression e2 = new Apply(new Apply(new Ident("(+)"), e0), e1);

        e2.findType(env);

        TypeChecker.unify(t0, ct);
        TypeChecker.unify(t1, ct);
    }
    
    @Test
    public void testInstanceChaining() throws HaskellException {
        Environment env = new HaskellCatalog().asEnvironment();

        // (id (id (1 :: Int)))
        Expression e0 = new Apply(new Ident("id"),
            new Apply(new Ident("id"),
                new Value(Type.con("Int"), "1")));

        Type t0 = e0.findType(env);

        assertEquals(t0.toHaskellType(), "Int");
    }
}

package nl.utwente.group10.haskell.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import nl.utwente.group10.haskell.expr.Hole;

public class TypeTest {
    @Test
    public final void toPrettyTypeTest() {
        TypeScope scope = new TypeScope();
        final Type t = Type.tupleOf(
                Type.listOf(scope.getVar("a")),
                Type.fun(
                        scope.getVar("b"),
                        Type.con("String")
                )
        );

        assertEquals("([a], b -> String)", t.prettyPrint());
    }

    @Test
    public final void getFreshTest() {
        TypeScope scope = new TypeScope();
        final Type t = Type.tupleOf(
                Type.listOf(scope.getVar("a")),
                Type.fun(
                        scope.getVar("b"),
                        Type.con("String")
                )
        );

        assertFalse(t == t.getFresh());
        assertEquals(t.prettyPrint(), t.getFresh().prettyPrint());
    }

    @Test
    public final void nestedFreshTest() throws HaskellTypeError {
        TypeScope scope = new TypeScope();
    	final TypeVar a = scope.getVar("a");
        final Type t = Type.tupleOf(Type.listOf(a), Type.listOf(a));                     
        final Type t2 = t.getFresh();
        
        assertEquals("([a], [a])", t.prettyPrint());
        assertEquals(t.prettyPrint(), t2.prettyPrint());

    	final TypeVar b = scope.getVar("b");
    	final Type i = Type.con("Int");
    	final Type t3 = Type.tupleOf(Type.listOf(i), Type.listOf(b));

    	TypeChecker.unify(new Hole(), t, t3);
    	assertEquals("([Int], [Int])", t.prettyPrint());

    	TypeChecker.unify(new Hole(), t2, t3);
    	assertEquals("([Int], [Int])", t2.prettyPrint());
    }
}

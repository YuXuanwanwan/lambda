package testsupport.traits;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monoid.builtin.Present;
import com.jnape.palatable.traitor.traits.Trait;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static java.util.Arrays.asList;

public class FunctorLaws<F extends Functor<?, F>> implements Trait<Functor<?, F>> {

    @Override
    public void test(Functor<?, F> f) {
        Present.<String>present((x, y) -> x + "\n\t - " + y)
                .<Fn1<Functor<?, F>, Maybe<String>>>foldMap(
                        fn -> fn.apply(f),
                        asList(this::testIdentity,
                               this::testComposition))
                .peek(s -> IO.throwing(new AssertionError("The following Functor laws did not hold for instance of " +
                                                                  f.getClass() + ": \n\t - " + s)));
    }

    private Maybe<String> testIdentity(Functor<?, F> f) {
        return f.fmap(id()).equals(f)
               ? nothing()
               : just("identity (f.fmap(identity()).equals(f))");
    }

    private Maybe<String> testComposition(Functor<?, F> functor) {
        Functor<Integer, F>   subject = functor.fmap(constantly(1));
        Fn1<Integer, Integer> f       = x -> x * 3;
        Fn1<Integer, Integer> g       = x -> x - 2;
        return subject.fmap(f.contraMap(g)).equals(subject.fmap(g).fmap(f))
               ? nothing()
               : just("composition (functor.fmap(f.contraMap(g)).equals(functor.fmap(g).fmap(f)))");
    }
}

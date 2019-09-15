package revolut.test.core;

public interface Transformer<I, O> {
    O transform(I object);
}

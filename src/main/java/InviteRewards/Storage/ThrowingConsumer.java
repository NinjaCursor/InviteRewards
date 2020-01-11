package InviteRewards.Storage;

import java.util.function.Consumer;

//https://stackoverflow.com/questions/18198176/java-8-lambda-function-that-throws-exception
@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {

    @Override
    default void accept(final T elem) {
        try {
            acceptThrows(elem);
        } catch (final Exception e) {
            // Implement your own exception handling logic here..
            // For example:
            System.out.println("handling an exception...");
            // Or ...
            throw new RuntimeException(e);
        }
    }

    void acceptThrows(T elem) throws Exception;

}

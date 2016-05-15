package jp.ambrosoli.salmon.test.util;

@FunctionalInterface
public interface RunnableToThrowException {

    void run() throws Exception;
}
